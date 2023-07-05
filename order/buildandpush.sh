#!/bin/bash
mvn clean install
docker build -t order .
sh ./gcp-artifact-deploy.sh
