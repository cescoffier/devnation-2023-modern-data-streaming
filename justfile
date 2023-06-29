# Build and Run tests
build:
    mvn clean install

# Build without test
quick:
    mvn clean install -DskipTests

# Build (without test) and push images to Quay
build-and-push-images: quick
    cd temperature-sensor  && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd camera-sensor  && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd kafka-topic-initializer && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd alert-manager && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd measure-enrichment && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd dashboard && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd picture-analyzer  && docker buildx build --platform=linux/amd64 -t quay.io/cescoffi/devnation-2023-picture-analyzer .  && docker push quay.io/cescoffi/devnation-2023-picture-analyzer

# Deploy kafka, the database, the picture analyzer and the routes
kube-prerequisites:
    kubectl apply -f kubernetes/kafka.yaml -f kubernetes/device-database.yaml -f kubernetes/alert-manager-route.yaml -f kubernetes/picture-analyzer.yaml -f kubernetes/dashboard-route.yaml
    kubectl wait pods -l name=kafka --for condition=Ready --timeout=90s
    kubectl wait pods -l name=device-database --for condition=Ready --timeout=90s
    kubectl wait pods -l app=picture-analyzer --for condition=Ready --timeout=200s

# Deploy everything but the sensors
deploy-to-kube: kube-prerequisites
    cd alert-manager && quarkus deploy kubernetes
    cd measure-enrichment && quarkus deploy kubernetes
    cd dashboard && quarkus deploy kubernetes
    echo "Alert-Manager route: https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`"
    echo "Dashboard route: https://`oc get routes -o json --field-selector metadata.name=dashboard | jq -r '.items[0].spec.host'`"

# Deploy the temperature sensors
create-thermometers:
    kubectl apply -f kubernetes/temperature-sensor.yaml

# Deploy the camera sensors
create-cameras:
    kubectl apply -f kubernetes/camera-sensor.yaml

# Deploy the misbehaving temperature sensors
create-bad-thermometers:
    kubectl apply -f kubernetes/bad-temperature-sensor.yaml

# Deploy the misbehaving camera sensors
create-bad-cameras:
    kubectl apply -f kubernetes/bad-camera-sensor.yaml

# Deploy all the devices (including the msbehaving ones)
create-all-devices: create-thermometers create-bad-thermometers create-cameras create-bad-cameras

# Delete all the sensors
kill-all-devices:
    kubectl delete pods -l app.kubernetes.io/part-of=sensors

# Delete all the misbehaving sensors
kill-bad-devices:
    kubectl delete pods -l bad=true

# Retrieve the temperature alerts
temperature-alert-stream:
 http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/temperatures --stream

# Retrieve the rabbit alerts
rabbit-alert-stream:
  http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/rabbits --stream

# Invoke the monitoring endpoint of the alert-managers
alert-monitoring:
 http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/monitoring

# Allows accessing the kafka service locally.
port-forward-kafka:
  jbang https://gist.githubusercontent.com/cescoffier/80cd4659895df854883cf1a3448e1390/raw/9447528ae6bfadb214a700000f92e1c1b2aa7b98/HostEdit.java --ip=127.0.0.1 --host=kafka
  kubectl port-forward svc/kafka 9092

port-forward-alert-manager:
    kubectl port-forward svc/alert-manager 8081:http

port-forward-measure-enrichment:
    kubectl port-forward svc/measure-enrichment 8082:http