DEFAULT_LABEL="0.0.1"
LABEL="${1:-$DEFAULT_LABEL}"
echo "Labeling: $LABEL"

docker build . -t us-west1-docker.pkg.dev/zerok-dev/java-spring-boot-demo/frontend:$LABEL
docker push us-west1-docker.pkg.dev/zerok-dev/java-spring-boot-demo/frontend:$LABEL
