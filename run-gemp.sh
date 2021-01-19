#!/usr/bin/env bash

rm -rf /env/gemp-swccg/web/Build
unzip -o gemp-swccg-async/target/web.zip -d /env/gemp-swccg/web/
cp gemp-swccg-async/target/web.jar /env/gemp-swccg/
sudo java -Xmx4g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlog4j.debug -Dlog4j.configuration=test-log4j.xml -cp /env/gemp-swccg/web.jar com.gempukku.swccgo.async.SwccgoAsyncServer
