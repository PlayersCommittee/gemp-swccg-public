#!/usr/bin/env bash

mkdir -p /env/gemp-swccg/web/images/cards
aws s3 cp s3://swccg-adamanthil/card-images/ /env/gemp-swccg/web/images/cards --recursive --region us-east-1 --no-sign-request
