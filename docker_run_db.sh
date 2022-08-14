#!/bin/bash

docker run -d \
  -p 3306:3306 \
  --name gempdb \
  -e MYSQL_ROOT_PASSWORD=gempukku \
  -e MYSQL_DATABASE=gemp-swccg \
  -e MYSQL_USER=gemp \
  -e MYSQL_PASSWORD=Four_mason8pirate \
  -d gempdb:latest

