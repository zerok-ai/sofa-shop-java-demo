###
#  Usage: 
#     ./setup.sh [apply|delete]
#  install/removes the deployment in specified Namespace.
### 

DEFAULT_NAMESPACE='zerok-demoapp'
APPLY_COMMAND='apply'
DELETE_COMMAND='delete'
COMMAND="${1:-$APPLY_COMMAND}"
NAMESPACE="${2:-DEFAULT_NAMESPACE}"

if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    kubectl create namespace $NAMESPACE
    kubectl $COMMAND -n $NAMESPACE -k ./
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" order/k8s/app-configmap_template.yaml > order/k8s/app-configmap.yaml
    sed -e "s/\${NAMESPACE}/$NAMESPACE/" inventory/k8s/app-configmap_template.yaml > inventory/k8s/app-configmap.yaml

elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then 
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi