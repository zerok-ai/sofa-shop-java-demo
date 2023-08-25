DEFAULT_LABEL="0.0.1"
LABEL="${1:-$DEFAULT_LABEL}"
echo "Labeling: $LABEL"

yarn build
docker build . -t us-west1-docker.pkg.dev/zerok-dev/sofa-shop/frontend:$LABEL
docker push us-west1-docker.pkg.dev/zerok-dev/sofa-shop/frontend:$LABEL

kubectl apply -k ./k8s
