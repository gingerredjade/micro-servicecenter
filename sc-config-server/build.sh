#!/usr/bin/env bash

mvn clean package -Dmaven.test.skip=true -U

docker build -t reg.cloud.com/mirrors-sc/sc-config-server:0.0.1-SNAPSHOT .

docker push reg.cloud.com/mirrors-sc/sc-config-server:0.0.1-SNAPSHOT
