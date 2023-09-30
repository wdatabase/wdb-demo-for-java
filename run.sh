#!/bin/bash

mvn clean package
java -jar -Dserver.port=8081 ./target/wdb_demo.war

