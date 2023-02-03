#!/bin/bash
mvn clean install
docker build -t inventory .
sh ./gcp-artifact-deploy.sh
