#!/bin/bash
THIS_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPTS_DIR="$(dirname "$THIS_DIR")"
source $SCRIPTS_DIR/variables.sh

dashboardName=$1

kubectl delete configmap ${dashboardName} --namespace monitoring

kubectl create configmap ${dashboardName} \
    --namespace monitoring \
	--from-file $setupfolder/../grafana/${dashboardName}.json \
    -o yaml --dry-run=client | kubectl apply -f -

kubectl label --overwrite --namespace monitoring configmap \
    ${dashboardName} \
    grafana_dashboard="1"

#perl -pi -e "s/pxclient02/pxclient02/" $setupfolder/../grafana/${dashboardName}.json