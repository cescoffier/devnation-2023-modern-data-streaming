build:
    mvn clean install

quick:
    mvn clean install -DskipTests

build-and-push-images: quick
    cd temperature-sensor  && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd camera-sensor  && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd kafka-topic-initializer && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd alert-manager && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd measure-enrichment && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd dashboard && quarkus build --clean -Dquarkus.container-image.push=true -DskipTests
    cd picture-analyzer  && docker buildx build --platform=linux/amd64 -t quay.io/cescoffi/devnation-2023-picture-analyzer .  && docker push quay.io/cescoffi/devnation-2023-picture-analyzer

kube-prerequisites:
    kubectl apply -f kubernetes/kafka.yaml -f kubernetes/device-database.yaml -f kubernetes/alert-manager-route.yaml -f kubernetes/picture-analyzer.yaml -f kubernetes/dashboard-route.yaml
    kubectl wait pods -l name=kafka --for condition=Ready --timeout=90s
    kubectl wait pods -l name=device-database --for condition=Ready --timeout=90s
    kubectl wait pods -l app=picture-analyzer --for condition=Ready --timeout=200s

deploy-to-kube: kube-prerequisites
    cd alert-manager && quarkus deploy kubernetes
    cd measure-enrichment && quarkus deploy kubernetes
    cd dashboard && quarkus deploy kubernetes
    echo "Alert-Manager route: https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`"

create-thermometers:
    kubectl apply -f kubernetes/temperature-sensor.yaml

create-cameras:
    kubectl apply -f kubernetes/camera-sensor.yaml

create-bad-thermometers:
    kubectl apply -f kubernetes/bad-temperature-sensor.yaml

create-bad-cameras:
    kubectl apply -f kubernetes/bad-camera-sensor.yaml

create-all-devices: create-thermometers create-bad-thermometers create-cameras create-bad-cameras

kill-all-devices:
    kubectl delete pods -l app.kubernetes.io/part-of=sensors

kill-bad-devices:
    kubectl delete pods -l bad=true

temperature-alert-stream:
 http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/temperatures --stream

rabbit-alert-stream:
  http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/rabbits --stream

alert-monitoring:
 http https://`oc get routes -o json --field-selector metadata.name=alert-manager | jq -r '.items[0].spec.host'`/monitoring