#!/usr/bin/env bash

mvn clean package -Dmaven.test.skip=true -U

docker build -t reg.gisnci.com/mirrors-gis/gis-register-center:0.0.1-SNAPSHOT .

docker push reg.gisnci.com/mirrors-gis/gis-register-center:0.0.1-SNAPSHOT
