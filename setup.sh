###
#  Usage: 
#     ./setup.sh [apply|delete]
#  install/removes the deployment in specified Namespace.
### 
scriptDir=$(dirname -- "$(readlink -f -- "$BASH_SOURCE")")

DEFAULT_NAMESPACE='zerok-demoapp'
APPLY_COMMAND='apply'
DELETE_COMMAND='delete'
COMMAND="${1:-$APPLY_COMMAND}"
NAMESPACE="${2:-$DEFAULT_NAMESPACE}"

if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    kubectl create namespace $NAMESPACE
    kubectl $COMMAND -n $NAMESPACE -k ${scriptDir}/
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" ${scriptDir}/order/k8s/app-configmap_template.yaml > ${scriptDir}/order/k8s/app-configmap.yaml
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" ${scriptDir}/inventory/k8s/app-configmap_template.yaml > ${scriptDir}/inventory/k8s/app-configmap.yaml

elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then 
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi