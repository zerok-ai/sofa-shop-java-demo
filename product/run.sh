export DB_HOST=localhost:3306
export DB_NAME=product-app-configmap
export DB_USERNAME=root
export DB_PASSWORD=

export ZK_OTEL_EXTENSION_PATH=../../zk-otel-extension

java -javaagent:$ZK_OTEL_EXTENSION_PATH/opentelemetry-javaagent.jar \
  -Dotel.javaagent.extensions=$ZK_OTEL_EXTENSION_PATH/build/libs/zk-otel-extension-1.0.jar \
  -Dotel.traces.exporter=zerok \
  -jar \
  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  target/product-0.0.1-SNAPSHOT.jar


