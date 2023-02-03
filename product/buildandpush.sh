#!/bin/bash
mvn clean install
docker build -t product .
sh ./gcp-artifact-deploy.sh
