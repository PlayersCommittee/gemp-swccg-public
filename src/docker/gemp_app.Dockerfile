FROM amazoncorretto:21-alpine-jdk

RUN apk update; \
	apk update \
	apk add --no-cache procps; \
	apk add --no-cache net-tools; \
	apk add --no-cache iputils; \
	apk add --no-cache bash; \
	apk add --no-cache util-linux; \
	apk add --no-cache dpkg; \
	apk add --no-cache gzip; \
	apk add --no-cache curl; \
	apk add --no-cache tar; \
	apk add --no-cache binutils; \
	apk add --no-cache freetype; \
	apk add --no-cache fontconfig; \
	apk add --no-cache git; \
	apk add --no-cache nano; 
		
		
#####################################################################
# The following is pulled from the official maven dockerfile:
# https://github.com/carlossg/docker-maven/blob/e545dcfa4d312e08330f4e07701763db3889db79/amazoncorretto-21/Dockerfile
#####################################################################

ENV MAVEN_HOME /usr/share/maven

COPY --from=maven:3.9.6-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY --from=maven:3.9.6-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml

RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Enables the JRE remote debugging; perhaps comment this out in a production build
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n


#####################################################################


WORKDIR /opt/gemp-swccg/src

