#!/bin/bash
mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true
mv target/planet-explorer-1.0-SNAPSHOT-jar-with-dependencies.jar .
java -jar planet-explorer-1.0-SNAPSHOT-jar-with-dependencies.jar