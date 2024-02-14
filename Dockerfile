FROM public.ecr.aws/ubuntu/ubuntu:focal

ENV DEBIAN_FRONTEND noninteractive

## Create gemp user
RUN useradd -r -s /bin/bash -m -d /opt/gemp-swccg -c gemp gemp

## Install dependencies
RUN apt-get update && \
    apt-get install -y unzip netcat nmap wget curl && \
    apt-get install -y openjdk-11-jre

## wait-for-it used for docker-compose testing
RUN wget -O /usr/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /usr/bin/wait-for-it.sh

## Set up GEMP directory structure
RUN mkdir -p /opt/gemp-swccg/web && \
    chown -R gemp:gemp /opt/gemp-swccg && \
    mkdir -p /logs && \
    chown -R gemp:gemp /logs

## a small quality of life addition for those times when entering the container
RUN echo 'export PS1="\u@\h:\w\$ "' > /etc/profile.d/ps1.sh && chmod +x /etc/profile.d/ps1.sh

COPY gemp-swccg-async/src/main/web \
     /opt/gemp-swccg/web

COPY gemp-swccg-async/target/web.jar \
     /opt/gemp-swccg/

## log4j configuration files for test and prod
COPY gemp-swccg-async/src/main/resources/test-log4j.xml \
     /opt/gemp-swccg/
COPY gemp-swccg-async/src/main/resources/prod-log4j.xml \
     /opt/gemp-swccg/

## Setup persistent volume shares for these directories
RUN mkdir -p /logs && chown gemp:gemp /logs
RUN mkdir -p /opt/gemp-swccg/replay && chown gemp:gemp /opt/gemp-swccg/replay

## do not run as the root user
USER gemp

WORKDIR /opt/gemp-swccg

## default parameters representing a test, non-prod, setup
ENV "application_root" "/opt/gemp-swccg"
ENV "db_hostname"      "gempdb"
ENV "db_dbname"        "gemp-swccg"
ENV "db_username"      "gemp"
ENV "db_password"      "Four_mason8pirate"
ENV "application_port" "8080"
ENV "web_path"         "/opt/gemp-swccg/web/"


## run the server
CMD ["/usr/bin/java", \
     "-Xmx4g", \
     "-Dlog4j.debug", \
     "-Dlog4j.configuration=prod-log4j.xml", \
     "-cp", "web.jar", \
     "com.gempukku.swccgo.async.SwccgoAsyncServer"]



