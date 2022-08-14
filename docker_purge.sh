#!/bin/bash

docker ps -a | awk '{print "docker rm -f "$1;}' | sh
docker rmi gemp
docker rmi gempdb
docker images | grep none | awk '{print "docker rmi "$3;}' | sh
