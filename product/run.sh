export DB_HOST=localhost:5432
export DB_NAME=productservice
export DB_USERNAME=postgres
export DB_PASSWORD=aSbkTm3DiB

export ZK_OTEL_EXTENSION_PATH=../../zk-otel-extension

java \
  -jar \
  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  target/product-0.0.1-SNAPSHOT.jar


