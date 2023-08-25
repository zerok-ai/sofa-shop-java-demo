###
#  Usage: 
#     ./setup.sh sofa-shop.mysql.<clustername>.getanton.com [apply|delete]
#  install/removes the deployment in specified Namespace.
### 
scriptDir=$(dirname -- "$(readlink -f -- "$BASH_SOURCE")")

DEFAULT_NAMESPACE='sofa-shop-mysql'
DEFAULT_EXTERNAL_HOSTNAME='sofa-shop.mysql.<clustername>.getanton.com'
APPLY_COMMAND='apply'
DELETE_COMMAND='delete'
EXTERNAL_HOSTNAME="${1:-$DEFAULT_EXTERNAL_HOSTNAME}"
COMMAND="${2:-$APPLY_COMMAND}"
NAMESPACE="${3:-$DEFAULT_NAMESPACE}"

echo "EXTERNAL_HOSTNAME=$EXTERNAL_HOSTNAME"

if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    kubectl create namespace $NAMESPACE
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" ${scriptDir}/order/k8s/app-configmap_template.yaml > ${scriptDir}/order/k8s/app-configmap.yaml
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" ${scriptDir}/inventory/k8s/app-configmap_template.yaml > ${scriptDir}/inventory/k8s/app-configmap.yaml

    sed -e "s/\${EXTERNAL_HOSTNAME}/$EXTERNAL_HOSTNAME/" ${scriptDir}/k8s/ingress-template.yaml > ${scriptDir}/k8s/ingress.yaml
    sed -e "s/\${EXTERNAL_HOSTNAME}/$EXTERNAL_HOSTNAME/" ${scriptDir}/k8s/managedCertificate-template.yaml > ${scriptDir}/k8s/managedCertificate.yaml

    kubectl $COMMAND -n $NAMESPACE -k ${scriptDir}/

    ips=($(kubectl get services -n ingress-nginx --no-headers --field-selector metadata.name=ingress-nginx-controller | awk '{print $4}'))
    gcp_dns_project=black-scope-358204
    domain=$EXTERNAL_HOSTNAME
    extip=$ips

    domain_exists=`gcloud dns --project="${gcp_dns_project}" record-sets list --name "${domain}" --zone="anton" --type="A" --format=yaml`

    if [ -z "$domain_exists" ] || [ "$domain_exists" == "" ]; then
       gcloud dns --project=$gcp_dns_project record-sets create $domain --zone=anton --type=A --rrdatas=$extip --ttl=10
    else
       gcloud dns --project=$gcp_dns_project record-sets update $domain --zone=anton --type=A --rrdatas=$extip --ttl=10
    fi

elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then 
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi
