#!/bin/bash
confirm () {
    read -r -p "${1:-Are you sure? [y/N]} " response
    case $response in
        [yY][eE][sS]|[yY])
            true
            ;;
        *)
            false
            ;;
    esac
}

get_abs_filename() {
  # $1 : relative filename
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

function init-mongo {
  docker run --rm --link ardoqdocker_mongodb_1:mongodb ardoq/data-seed mongorestore -h mongodb /work/dist_seed/
  docker run --rm --volumes-from ardoqdocker_api_1 ardoq/data-seed:latest cp -r /work/attachments /data
}

function backup {
  if [ ! -d "$1" ]; then
    echo "Please specify a valid directory to store backups"
  else
    echo "Backing up into $1"
    docker run --rm -i -t -v $1:/dbbackup --link ardoqdocker_mongodb_1:mongodb mongo /bin/bash -c \
      'export DATE=$(date +%Y-%m-%d_%Hh%Mm) ; mongodump -h mongodb -o /dbbackup/$DATE ; cd /dbbackup ; tar czf mongodb-$DATE.tar.gz $DATE ; rm -r $DATE'
    docker run --rm -i -t -v $1:/attachments --volumes-from ardoqdocker_data_1 alpine:3.3 /bin/sh -c \
      'export DATE=$(date +%Y-%m-%d_%Hh%Mm) ; cd /data/attachments ; tar czf /attachments/attachments-$DATE.tar.gz workspace'
    SNAPSHOT=$(date +%Y-%m-%d_%Hh%Mm)
    docker commit ardoqdocker_elasticsearch_1 es_backup-$SNAPSHOT
    docker save es_backup-$SNAPSHOT > $1/es_backup-$SNAPSHOT.tar
  fi
}

function restore {
  docker run --rm -i -t -v "$1":/dbbackup/mongodb.tar.gz --link ardoqdocker_mongodb_1:mongodb mongo /bin/bash -c \
      'cd /dbbackup; mkdir unzipped; tar xzf mongodb.tar.gz --directory unzipped --strip 1; mongorestore -h mongodb --drop unzipped ; rm -r unzipped'

  docker run --rm -it -v "$2":/backup/attachments.tar.gz  --volumes-from ardoqdocker_data_1 alpine:3.3 /bin/sh -c \
      'cd /data/attachments ; tar xzf /backup/attachments.tar.gz'
}

function update-help-db {
  docker run --rm --link ardoqdocker_mongodb_1:mongodb ardoq/data-seed mongorestore -h mongodb --drop --db ardoq-common /work/dist_seed/ardoq-common
  docker run --rm --link ardoqdocker_mongodb_1:mongodb ardoq/data-seed mongorestore -h mongodb --drop --db shared /work/dist_seed/shared
  docker run --rm --volumes-from ardoqdocker_api_1 ardoq/data-seed:latest cp -r /work/attachments /data
}

function clean {
  confirm "Perform backup before deleting containers? [y/N]" && mkdir -p ardoq-data-backup && backup  $(get_abs_filename ardoq-data-backup)
  docker-compose -f ardoq.yml -p ardoqdocker stop
  docker-compose -f ardoq.yml -p ardoqdocker rm -f
}

function load-offline {
  tar -xf $1
  for FILE in $(ls ardoq-offline); do
      if [[ $FILE == *.tar ]]; then
          echo "Loading ardoq-offline/$FILE"
          docker load -i ardoq-offline/$FILE
      fi
  done
  rm -rf ardoq-offline
}


if [ $# -eq 0 ] || [ "$1" == "--help" ]; then
    cat <<EOF
Wrapper around docker-compose to run with correct file and projectname in production

Usage:
  ./ardoq.sh [options] [COMMAND] [ARGS...]
  ./ardoq.sh -h |--help

Commands:
  start             Create and start containers in daemon mode
  stop              Stop containers
  ps                List containers
  logs              View output from containers
  pull              Pulls service images from Docker Hub
  load <file.tar>   Loads zipped images from specified archive folder into local Docker registry 
  init              Initializes Ardoq databases. ONLY first time install!
  update            Applies latest version help content and demo content. Requires "pull" or "load" to actually have updated content to apply.
  rm                Stops and deletes all Ardoq containers. Data is backed up to folder "ardoq-data-backup", and is also persisted within docker for the updated version to use.
  backup <dir>      Backs up Ardoq into specified folder
  restore <db-backup> <attachments-backup> 
                    Restores database and attachments from backup  

EOF
else
    case "$1" in
        start)
            docker-compose -f ardoq.yml -p ardoqdocker up -d
            ;;
        dev)
            docker-compose -f ardoq.yml -p ardoqdocker up -d redis mongodb
            ;;
        api)
            docker-compose -f ardoq.yml -p ardoqdocker up -d api redis mongodb
            ;;
        stop)
            docker-compose -f ardoq.yml -p ardoqdocker stop
            ;;
        ps)
            docker-compose -f ardoq.yml -p ardoqdocker ps
            ;;
        logs)
            docker-compose -f ardoq.yml -p ardoqdocker logs
            ;;
        pull)
            docker-compose -f ardoq.yml -p ardoqdocker pull
            docker pull mongo:latest
            docker pull ardoq/data-seed:latest
            ;;
        load)
            if [ ! -f "$2" ]; then
                echo "Please specify a valid file (ardoq-offline.tgz) to load images from."
                exit 1
            fi
            load-offline $2
            ;;
        init)
            confirm "This will install Ardoq default databases. Are you sure you wish to continue? [y/N]" && init-mongo
            ;;
        update)
            update-help-db
            ;;
        up)
            docker-compose -f ardoq.yml -p ardoqdocker $@
            ;;
        rm)
            clean
            ;;
        backup)
            backup $(get_abs_filename $2)
            ;;
        restore)
            if [ ! -f "$2" ] || [ ! -f "$3" ]; then
                echo "Please specify both a database backup file and attachments backup file to read backup from."
                exit 1
            fi
            confirm "This will overwrite and replace your existing database. Are you sure? [y/N]" && restore $(get_abs_filename $2) $(get_abs_filename $3)
            ;;
        *)
            echo "Unknown command"
            ;;
    esac
fi
