#!/bin/bash
###
#  Usage: 
#     ./setup.sh [apply|delete]
#  install/removes the deployment in specified Namespace.
### 
scriptDir=$(dirname -- "$(readlink -f -- "$BASH_SOURCE")")
source $scriptDir/variables.sh

COMMAND=$APPLY_COMMAND

show_help() {
  echo "Usage: ./setup.sh [options]"
  echo "Options:"
  echo "  -e <external_hostname>: Specify the external hostname for the deployment."
  echo "  -c <command>: Specify the command (apply or delete). Default: $APPLY_COMMAND"
  echo "  -n <namespace>: Specify the namespace for the deployment. Default: $DEFAULT_NAMESPACE"
  echo "  -m <mode>: Specify the mode (postgres or mysql). Default: $MODE_MYSQL"
  echo "  -h: Show this help message."
  echo ""
  echo "For more information on how default values are calculated, refer to the README.md file"

  exit 0
}

while getopts "e:c:n:m:h" opt; do
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
  EXTERNAL_HOSTNAME="sofa-shop.$MODE.$CLUSTER_NAME.getanton.com"
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
echo "scriptDir=$scriptDir"
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

if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    # Check if the namespace exists
    if kubectl get namespace "$NAMESPACE" >/dev/null 2>&1; then
      echo "Namespace $NAMESPACE already exists."
    else
      # Create the namespace
      kubectl create namespace "$NAMESPACE"
    fi

    #Inventory Setup
    envsubst < ${scriptDir}/inventory/k8s/app-configmap_template.yaml > ${scriptDir}/inventory/k8s/app-configmap.yaml
    envsubst < ${scriptDir}/inventory/k8s/db-secrets_template.yaml > ${scriptDir}/inventory/k8s/db-secrets.yaml

    #Order Setup
    envsubst < ${scriptDir}/order/k8s/app-configmap_template.yaml > ${scriptDir}/order/k8s/app-configmap.yaml
    envsubst < ${scriptDir}/order/k8s/db-secrets_template.yaml > ${scriptDir}/order/k8s/db-secrets.yaml

    #Product Setup
    envsubst < ${scriptDir}/product/k8s/app-configmap_template.yaml > ${scriptDir}/product/k8s/app-configmap.yaml
    envsubst < ${scriptDir}/product/k8s/db-secrets_template.yaml > ${scriptDir}/product/k8s/db-secrets.yaml

    #Loadrun
    envsubst < ${scriptDir}/k8s/external/loadrun-deployment_template.yaml > ${scriptDir}/k8s/external/loadrun-deployment.yaml

    #k8s
    envsubst < ${scriptDir}/k8s/ingress-template.yaml > ${scriptDir}/k8s/ingress.yaml
    envsubst < ${scriptDir}/k8s/managedCertificate-template.yaml > ${scriptDir}/k8s/managedCertificate.yaml

    kubectl $COMMAND -n $NAMESPACE -k ${scriptDir}/
    kubectl $COMMAND -k ${scriptDir}/k8s/external/

    ips=($(kubectl get services -n ingress-nginx --no-headers --field-selector metadata.name=ingress-nginx-controller | awk '{print $4}'))
    gcp_dns_project=black-scope-358204
    domain=$EXTERNAL_HOSTNAME
    extip=$ips
    isIP=`echo "$extip" | awk '/^([0-9]{1,3}[.]){3}([0-9]{1,3})$/{print $1}'`

    if [ -z $isIP ]; then
        echo "Updating CNAME record for $domain to $extip"
        domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${domain}" --zone="anton" --type="CNAME" --format=yaml`

        if [ -z "$domain_exists" ] || [ "$domain_exists" == "" ]; then
        gcloud dns --project=$gcp_dns_project record-sets create $domain --zone=anton --type="CNAME" --rrdatas="$extip." --ttl=10
        else
        gcloud dns --project=$gcp_dns_project record-sets update $domain --zone=anton --type="CNAME" --rrdatas="$extip." --ttl=10
        fi
    else
        echo "Updating A record for $domain to $extip"
        domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${domain}" --zone="anton" --type="A" --format=yaml`

        if [ -z "$domain_exists" ] || [ "$domain_exists" == "" ]; then
        gcloud dns --project=$gcp_dns_project record-sets create $domain --zone=anton --type=A --rrdatas=$extip --ttl=10
        else
        gcloud dns --project=$gcp_dns_project record-sets update $domain --zone=anton --type=A --rrdatas=$extip --ttl=10
        fi

    fi

elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi
