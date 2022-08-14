#!/bin/bash


## Build Gemp
mvn clean install
if [ $? == 1 ]; then
  echo
  echo "Java build failed. Not building Docker Container."
  echo
  exit 1
fi

## Build Gemp Container Image
docker build \
  -t gemp:latest \
  -f Dockerfile .

## Build Database Container Image
docker images 1>/dev/null 2>&1 | grep gempdb
if [ $? == 1 ]; then
  echo
  echo "Building Gemp Database server image. Will only build once."
  echo
  docker build --force-rm=true --no-cache \
    -t gempdb:latest \
    -f db.Dockerfile .
fi
