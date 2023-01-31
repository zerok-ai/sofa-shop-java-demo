###
#  Usage: 
#     ./setup.sh [apply|delete]
#  install/removes the deployment in specified Namespace.
### 

NAMESPACE='zerok-demoapp'
APPLY_COMMAND='apply'
DELETE_COMMAND='delete'
COMMAND="${1:-$APPLY_COMMAND}"

if [[ "$COMMAND" == "$APPLY_COMMAND" ]]
then
    kubectl create namespace $NAMESPACE
    kubectl $COMMAND -n $NAMESPACE -k ./
elif [[ "$COMMAND" == "$DELETE_COMMAND"  ]]
then 
    kubectl $COMMAND namespace $NAMESPACE
else
    echo "Invalid option provided"
fi