#!/bin/bash

docker stop gemp 1>/dev/null 2>&1 ; true

docker rm gemp 1>/dev/null 2>&1 ; true

docker run -d \
  -p 8080:8080 \
  --name gemp \
  --link gempdb \
  -e db_hostname=gempdb \
  -e playtesting_no_limit_deck_length=true \
  gemp:latest

