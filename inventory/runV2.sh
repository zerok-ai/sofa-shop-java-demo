export DB_HOST=localhost:5432
export DB_NAME=inventoryservice
export DB_USERNAME=postgres
export DB_PASSWORD=aSbkTm3DiB
export PRODUCT_HOST=hello


scriptDir=$(dirname -- "$(readlink -f -- "$BASH_SOURCE")")
parent_dir=$(dirname "$scriptDir")
source $parent_dir/variables.sh

EXTERNAL_HOSTNAME=$DEFAULT_EXTERNAL_HOSTNAME
COMMAND=$APPLY_COMMAND
MODE=$MODE_POSTGRES
NAMESPACE="$DEFAULT_NAMESPACE-$MODE"

while getopts "e:c:n:m:" opt; do
  case $opt in
    e)
      EXTERNAL_HOSTNAME="${OPTARG:-$DEFAULT_EXTERNAL_HOSTNAME}"
      ;;
    c)
      COMMAND="${OPTARG:-$APPLY_COMMAND}"
      ;;
    n)
      NAMESPACE="${OPTARG:-$DEFAULT_NAMESPACE}-$MODE"
      ;;
    m)
      MODE="${OPTARG:-$MODE_POSTGRES}"
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done

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
  export DB_HOST="$MYSQL_HOST_LOCAL"
  export DB_USERNAME="$MYSQL_USERNAME_SECRET"
  export DB_USERNAME=$(echo "$DB_USERNAME" | base64 -d)
  export DB_PASSWORD="$MYSQL_PASSWORD_SECRET"
  export DB_PASSWORD=$(echo "$DB_PASSWORD" | base64 -d)
  export DB_URL_PARAMS="$MYSQL_URL_PARAMS"
elif [[ "$MODE" == "$MODE_POSTGRES" ]]; then
  export DB_DRIVER_TYPE="$PS_DRIVER_TYPE"
  export DB_DRIVER_CLASS="$PS_DRIVER_CLASS"
  export DB_PRINT_SQL="$DB_PRINT_SQL"
  export DB_URL_PARAMS="$PS_URL_PARAMS"
  export DB_HIBERNATE_DIALECT="$PS_DRIVER_DIALECT"
  export DB_HOST="$PS_HOST_LOCAL"
  export DB_USERNAME="$PS_USERNAME_SECRET"
  export DB_USERNAME=$(echo "$DB_USERNAME" | base64 -d)
  export DB_PASSWORD="$PS_PASSWORD_SECRET"
  export DB_PASSWORD=$(echo "$DB_PASSWORD" | base64 -d)
  export DB_URL_PARAMS="$PS_URL_PARAMS"
else
  echo "Invalid MODE specified."
  exit 1
fi


echo "scriptDir=$scriptDir"
echo "EXTERNAL_HOSTNAME=$EXTERNAL_HOSTNAME"
echo "COMMAND=$COMMAND"
echo "NAMESPACE=$NAMESPACE"
echo "MODE=$MODE"
echo "DB_DRIVER_TYPE: $DB_DRIVER_TYPE"
echo "DB_DRIVER_CLASS: $DB_DRIVER_CLASS"
echo "DB_PRINT_SQL: $DB_PRINT_SQL"
echo "DB_URL_PARAMS: $DB_URL_PARAMS"
echo "DB_HIBERNATE_DIALECT: $DB_HIBERNATE_DIALECT"
echo "DB_HOST: $DB_HOST"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_PASSWORD: $DB_PASSWORD"
echo "DB_URL_PARAMS: $DB_URL_PARAMS"
echo "PRODUCT_HOST: $PRODUCT_HOST"

export ZK_OTEL_EXTENSION_PATH=../../zk-otel-extension

#java -javaagent:$ZK_OTEL_EXTENSION_PATH/opentelemetry-javaagent.jar \
#  -Dotel.javaagent.extensions=$ZK_OTEL_EXTENSION_PATH/build/libs/zk-otel-extension-1.0-all.jar \
#  -Dotel.traces.exporter=zerok \
#  -jar target/inventory-0.0.1-SNAPSHOT.jar

java -jar target/inventory-0.0.1-SNAPSHOT.jar