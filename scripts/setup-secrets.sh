#!/bin/bash
THIS_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
source $THIS_DIR/variables.sh


echo ''
echo '-----------------SETTING-UP-SECRETS-----------------'

rm -f $THIS_DIR/modified/cloud_ingress_*.yaml
rm -f $THIS_DIR/modified/certificate_*.yaml
envsubst < $THIS_DIR/originals/certificate_cloud_proxy_tls_certs.yaml > $THIS_DIR/modified/certificate_cloud_proxy_tls_certs.yaml
envsubst < $THIS_DIR/originals/certificate_cloud_proxy_tls_certs_nginx.yaml > $THIS_DIR/modified/certificate_cloud_proxy_tls_certs_nginx.yaml

FOUND_LETSENCRYPT_CLUSTERISSUER='0'

clusterissuers=($(kubectl get clusterissuer --no-headers | awk '{print $1}'))
for i in "${!clusterissuers[@]}"; do
  if [[ ${clusterissuers[i]} == "letsencrypt-cluster-issuer" ]]; then
    FOUND_LETSENCRYPT_CLUSTERISSUER='1'
  fi
done

if [ "$FOUND_LETSENCRYPT_CLUSTERISSUER" == '0' ]
then
  echo 'ClusterIssuer is not present. Creating one...'
  kubectl apply -f $THIS_DIR/clusterissuer.yaml
  sleep $SETUP_CLUSTERISSUER_WAIT_TIME
fi

CERTIFICATES_COUNT=0
ADD_WAIT_TIME='0'

certificates=($(kubectl get certificates -A --no-headers | awk '{print $2}'))
for i in "${!certificates[@]}"; do
  if [[ ${certificates[i]} == "cloud-proxy-tls-certs" ]]; then
    ((CERTIFICATES_COUNT++))
  fi
done

if [[ $CERTIFICATES_COUNT -lt 2 ]]
then
  kubectl apply -f $THIS_DIR/modified/certificate_cloud_proxy_tls_certs.yaml
  kubectl apply -f $THIS_DIR/modified/certificate_cloud_proxy_tls_certs_nginx.yaml
  ADD_WAIT_TIME='1'
else
  echo "Secrets already present"
fi

if [ "$ADD_WAIT_TIME" == '1' ]
then
  echo "Waiting for the secrets to come up... (wait time $SETUP_SECRETS_WAIT_TIME seconds)"
  sleep $SETUP_SECRETS_WAIT_TIME
fi



