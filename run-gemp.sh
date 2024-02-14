#!/usr/bin/env bash

rm -rf /env/gemp-swccg/web/Build
unzip -o gemp-swccg-async/target/web.zip -d /env/gemp-swccg/web/
if [ -f gemp-swccg-async/target/web.jar ]; then
  cp gemp-swccg-async/target/web.jar /env/gemp-swccg/
else
  echo
  echo "Unable to find gemp-swccg-async/target/web.jar"
  echo "Build gemp before trying to run it."
  echo
  exit 1
fi
java -Xmx4g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlog4j.debug -Dlog4j.configuration=test-log4j.xml -cp /env/gemp-swccg/web.jar com.gempukku.swccgo.async.SwccgoAsyncServer
