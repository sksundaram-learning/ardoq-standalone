#!/bin/bash

CONTAINER_NAME=kibana
MYDIR="$(dirname "$(which "$0")")"

if [ "$1" == "start" ]; then
    KIBANA_STAUS=$("$MYDIR"/check_container.sh $CONTAINER_NAME)
    case "$?" in
        3) echo "Container does not exist, starting new container"
            docker run -d --name $CONTAINER_NAME -p 8080:5601 -e ELASTICSEARCH_URL=http://es:9200 -e ES_SCHEME=http --link ardoqdocker_elasticsearch_1:es kibana:4.1.1
            ;;
        2) echo "Container exists, but is not running - starting container..."
            docker start kibana
            ;;
        *) echo "Container already running"
    esac
elif [ "$1" == "stop" ]; then
    RUNNING=$(docker inspect --format="{{ .State.Running }}" $CONTAINER_NAME 2> /dev/null)
    if [ "$RUNNING" == "true" ]; then
        echo "Stopping container $CONTAINER_NAME"
        docker stop $CONTAINER_NAME
    else
        echo "$CONTAINER_NAME already stoped"
    fi
else
    echo "Valid commands: start|stop"
fi
