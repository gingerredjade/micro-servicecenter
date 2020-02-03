#!/usr/bin/env bash

java -jar gis-register-center-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone --server.port=8761

#nohup java -jar gis-register-center-0.0.1-SNAPSHOT.jar --spring.profiles.active=standalone --server.port=8761 > console-gis-register-center.file 2>&1 &

