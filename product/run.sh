export DB_HOST=localhost:5432
export DB_NAME=productservice
export DB_USERNAME=postgres
export DB_PASSWORD=aSbkTm3DiB

export ZK_OTEL_EXTENSION_PATH=../../zk-otel-extension

java -javaagent:$ZK_OTEL_EXTENSION_PATH/opentelemetry-javaagent.jar \
  -Dotel.javaagent.extensions=$ZK_OTEL_EXTENSION_PATH/build/libs/zk-otel-extension-1.0.jar \
  -Dotel.traces.exporter=zerok \
  -jar \
  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  target/product-0.0.1-SNAPSHOT.jar


