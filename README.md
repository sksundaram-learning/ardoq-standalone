#Ardoq standalone

The entire Ardoq stack is packaged using Docker. 

Ardoq will run on a single server that is set up to run Docker Compose. 

The docker images we use are either official releases, or based on the Alpine Linux distribution. Alpine Linux is a minimal Linux distribution, with only the bare necessities installed. In addition to giving small, efficient images. This is also beneficial for security, as the attack surface is smaller.  

## Prerequisites

- A server, physical or virtual, that supports Docker Machine - [options here](https://docs.docker.com/installation/ target= target= target= target= target= target="_blank")  
- At least 8 GB RAM
- At least 100 GB Disk space
- [Docker Compose](https://docs.docker.com/compose/install/ target="_blank")  
- Access to the Ardoq Docker images on [Docker Hub](http://hub.docker.com target= target= target= target= target= target="_blank")  
  , or zipped on file (contact Ardoq)

  

#### Scripts and Environment

Along with this installation instruction, you should have received a copy of the docker-compose file _**ardoq\.yml**_ and the startup script **_ardoq.sh._**  
  

## Ardoq Docker Images

To start the ardoq Docker stack, you need to have the Ardoq Docker images\. You can obtain these in two ways \- either by downloading them from our repository, or from a zipped archive\.&nbsp;                

#### A) Online Images from Docker Hub

Docker Hub is a public and private repository for Docker images. To get access to Ardoq's private images, you need a user at hub.docker.io, before Ardoq can grant you access to our private repositories. 

Log your command line client in to Dockerhub with:  

```
docker login

```

Verify that you have access to our private images, for example with:              

```
docker pull ardoq/ardoq-front:latest

```

Download the all the latest Docker images              

```
./ardoq.sh pull

```

#### B) Offline Images from Zipped Archives

Get our Docker images for offline installation. 

Load into your local docker registry with:  

```
./ardoq.sh load [path/to/offline/distribution/ardoq-offline.tar]

```
  

## Running the Ardoq Docker stack

To start the Ardoq stack, execute:                

```
./ardoq.sh start

```

Show the logs with:              

```
./ardoq.sh logs

```

#### Bootstrapping the Ardoq database - install time only!

When installing a fresh Ardoq instance, you need to bootstrap the database\. This is done with the command                

```
./ardoq.sh init 

```

NB! Don't init when you have an exisiting database! 

The initial database includes an user **admin** with password **ardoq123**. Please change this!  
  

## Maintaining the Ardoq Docker Stack

#### Backup

Please schedule your backups to be stored automatically at regular intervals:                

```
./ardoq.sh backup /your/local/folder/to/store/the/backup

```

The backup script spawns a new Mongo\-DB client in a Docker container, with a link to the running Mongo\-DB container, and exports the data to the specified folder, then exits\. Attachments and logs are also backed up\.              

#### Restore

Be aware that the restore procedure will overwrite the existing database, if present\!                

```
./ardoq.sh restore [path/to/database/backup.tar] [path/to/attachment/backup.tar]

```

#### Upgrade

Upgrade should be performed at regular intervals to keep up with the latest improvements of the product. 

1) Download or import the latest images as described under**Ardoq Docker Images** 

2) Stop the Ardoq application stack:  

```
./ardoq.sh stop

```

NB\! This will stop the application, so users will not be able to work while the update is performed\!   
3\) Start Ardoq    

```
./ardoq.sh start

```

4\) Import updated help content              

```
./ardoq.sh update

```


#### Custom Authentication / Active Directory Integration

Custom integration must be tailored for each installation. This is done by implementing a small Java program that is installed on the API server. The program will be executed on every login, and can forward authentication requests to any local authentication system. We provide a scaffolding project for LDAP authentication 

[details on how this works here](custom-authentication/README.md)


## Print me
[PDF export of this document](https://gitprint.com/ardoq/ardoq-standalone/master/README.md)
