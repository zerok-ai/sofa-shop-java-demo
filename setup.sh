#!/bin/bash
###
#  Usage: 
#     ./setup.sh [apply|delete]
#  install/removes the deployment in specified Namespace.
### 
thisDir=$(dirname -- "$(readlink -f -- "$BASH_SOURCE")")
SCRIPTS_DIR=$thisDir/scripts
source $SCRIPTS_DIR/variables.sh

COMMAND=$APPLY_COMMAND
SSL_SETUP=1

show_help() {
  echo "Usage: ./setup.sh [options]"
  echo "Options:"
  echo "  -e <external_hostname>: Specify the external hostname for the deployment."
  echo "  -c <command>: Specify the command (apply or delete). Default: $APPLY_COMMAND"
  echo "  -n <namespace>: Specify the namespace for the deployment. Default: $DEFAULT_NAMESPACE"
  echo "  -m <mode>: Specify the mode (postgres or mysql). Default: $MODE_MYSQL"
  echo "  -s <ssl>: Is ssl needs to be setup (postgres or mysql) or not Default: 0 (false)"
  echo "  -h: Show this help message."
  echo ""
  echo "For more information on how default values are calculated, refer to the README.md file"

  exit 0
}

while getopts "e:c:n:m:h:s" opt; do
  case $opt in
    e)
      EXTERNAL_HOSTNAME="${OPTARG:-$DEFAULT_EXTERNAL_HOSTNAME}"
      ;;
    c)
      COMMAND="${OPTARG:-$APPLY_COMMAND}"
      ;;
    m)
      MODE="${OPTARG:-$MODE_MYSQL}"
      ;;
    n)
      NAMESPACE="${OPTARG:-$DEFAULT_NAMESPACE-$MODE}"
      ;;
    s)
      SSL_SETUP="${OPTARG:-0}"
      ;;
    h)
      show_help
      exit 1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done

if [[ -z "$MODE" ]]; then
  MODE="$MODE_MYSQL"
fi

if [[ -z "$NAMESPACE" ]]; then
  NAMESPACE="$DEFAULT_NAMESPACE-$MODE"
fi



if [[ -z "$EXTERNAL_HOSTNAME" ]]; then
  echo "Here MODE=$MODE"
  CLUSTER_NAME=$(kubectl config current-context | awk -F '_' '{print $4}')
  export CLUSTER_DOMAIN="$CLUSTER_NAME.$DOMAIN"
  EXTERNAL_HOSTNAME="sofa-shop.$MODE.$CLUSTER_DOMAIN"
fi

export EXTERNAL_HOSTNAME=$EXTERNAL_HOSTNAME
export COMMAND=$COMMAND
export MODE=$MODE
export NAMESPACE=$NAMESPACE

if [[ "$MODE" == "$MODE_MYSQL" ]]; then
  export DB_DRIVER_TYPE="$MYSQL_DRIVER_TYPE"
  export DB_DRIVER_CLASS="$MYSQL_DRIVER_CLASS"
  export DB_PRINT_SQL="$DB_PRINT_SQL"
  export DB_URL_PARAMS="$MYSQL_URL_PARAMS"
  export DB_HIBERNATE_DIALECT="$MYSQL_DRIVER_DIALECT"
  export DB_HOST="$MYSQL_HOST"
  if [[ -z "$DB_USERNAME" ]]; then
    export DB_USERNAME="$MYSQL_USERNAME_SECRET"
  fi
  if [[ -z "$DB_PASSWORD" ]]; then
    export DB_PASSWORD="$MYSQL_PASSWORD_SECRET"
  fi
  export DB_URL_PARAMS="$MYSQL_URL_PARAMS"
elif [[ "$MODE" == "$MODE_POSTGRES" ]]; then
  export DB_DRIVER_TYPE="$PS_DRIVER_TYPE"
  export DB_DRIVER_CLASS="$PS_DRIVER_CLASS"
  export DB_PRINT_SQL="$DB_PRINT_SQL"
  export DB_URL_PARAMS="$PS_URL_PARAMS"
  export DB_HIBERNATE_DIALECT="$PS_DRIVER_DIALECT"
  export DB_HOST="$PS_HOST"
  if [[ -z "$DB_USERNAME" ]]; then
    export DB_USERNAME="$PS_USERNAME_SECRET"
  fi
  if [[ -z "$DB_PASSWORD" ]]; then
    export DB_PASSWORD="$PS_PASSWORD_SECRET"
  fi
  export DB_URL_PARAMS="$PS_URL_PARAMS"
else
  echo "Invalid MODE specified."
  exit 1
fi


echo "DEFAULT_EXTERNAL_HOSTNAME=$DEFAULT_EXTERNAL_HOSTNAME"
echo "thisDir=$thisDir"
echo "EXTERNAL_HOSTNAME=$EXTERNAL_HOSTNAME"
echo "COMMAND=$COMMAND"
echo "NAMESPACE=$NAMESPACE"
echo "MODE=$MODE"
echo 
echo "DB_DRIVER_TYPE: $DB_DRIVER_TYPE"
echo "DB_DRIVER_CLASS: $DB_DRIVER_CLASS"
echo "DB_PRINT_SQL: $DB_PRINT_SQL"
echo "DB_URL_PARAMS: $DB_URL_PARAMS"
echo "DB_HIBERNATE_DIALECT: $DB_HIBERNATE_DIALECT"
echo "DB_HOST: $DB_HOST"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_PASSWORD: $DB_PASSWORD"
echo "DB_URL_PARAMS: $DB_URL_PARAMS"

#########
kubectl create namespace $NAMESPACE
$SCRIPTS_DIR/setup-dns.sh

echo "SSL_SETUP=$SSL_SETUP"
if [[ "$SSL_SETUP" == "1" ]]; then
  $SCRIPTS_DIR/setup-cert-manager.sh
  $SCRIPTS_DIR/setup-secrets.sh
fi


if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    # Check if the namespace exists
    if kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
      echo "Namespace $NAMESPACE already exists."
    else
      # Create the namespace
      kubectl create namespace "$NAMESPACE"
    fi

#    kubectl label --overwrite namespace $NAMESPACE zk-injection=enabled

    #Inventory Setup
    envsubst < ${thisDir}/inventory/k8s/app-configmap_template.yaml > ${thisDir}/inventory/k8s/app-configmap.yaml
    envsubst < ${thisDir}/inventory/k8s/db-secrets_template.yaml > ${thisDir}/inventory/k8s/db-secrets.yaml

    #Order Setup
    envsubst < ${thisDir}/order/k8s/app-configmap_template.yaml > ${thisDir}/order/k8s/app-configmap.yaml
    envsubst < ${thisDir}/order/k8s/db-secrets_template.yaml > ${thisDir}/order/k8s/db-secrets.yaml

    #Product Setup
    envsubst < ${thisDir}/product/k8s/app-configmap_template.yaml > ${thisDir}/product/k8s/app-configmap.yaml
    envsubst < ${thisDir}/product/k8s/db-secrets_template.yaml > ${thisDir}/product/k8s/db-secrets.yaml

    #Loadrun
    envsubst < ${thisDir}/k8s/external/loadrun-deployment_template.yaml > ${thisDir}/k8s/external/loadrun-deployment.yaml

    #k8s
    envsubst < ${thisDir}/k8s/ingress-template.yaml > ${thisDir}/k8s/ingress.yaml
    envsubst < ${thisDir}/k8s/managedCertificate-template.yaml > ${thisDir}/k8s/managedCertificate.yaml

    kubectl $COMMAND -n $NAMESPACE -k ${thisDir}/
    kubectl $COMMAND -k ${thisDir}/k8s/external/
#
#    ips=($(kubectl get services -n ingress-nginx --no-headers --field-selector metadata.name=ingress-nginx-controller | awk '{print $4}'))
#    gcp_dns_project=black-scope-358204
#    domain=$EXTERNAL_HOSTNAME
#    extip=$ips
#    isIP=`echo "$extip" | awk '/^([0-9]{1,3}[.]){3}([0-9]{1,3})$/{print $1}'`
#
#    if [ -z $isIP ]; then
#        echo "Updating CNAME record for $domain to $extip"
#        domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${domain}" --zone="zerok-dev" --type="CNAME" --format=yaml`
#
#        if [ -z "$domain_exists" ] || [ "$domain_exists" == "" ]; then
#        gcloud dns --project=$gcp_dns_project record-sets create $domain --zone=zerok-dev --type="CNAME" --rrdatas="$extip." --ttl=10
#        else
#        gcloud dns --project=$gcp_dns_project record-sets update $domain --zone=zerok-dev --type="CNAME" --rrdatas="$extip." --ttl=10
#        fi
#    else
#        echo "Updating A record for $domain to $extip"
#        domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${domain}" --zone="zerok-dev" --type="A" --format=yaml`
#
#        if [ -z "$domain_exists" ] || [ "$domain_exists" == "" ]; then
#        gcloud dns --project=$gcp_dns_project record-sets create $domain --zone=zerok-dev --type=A --rrdatas=$extip --ttl=10
#        else
#        gcloud dns --project=$gcp_dns_project record-sets update $domain --zone=zerok-dev --type=A --rrdatas=$extip --ttl=10
#        fi
#
#    fi

elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi
