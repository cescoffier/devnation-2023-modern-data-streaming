# DevNation Day: Modern App Dev - Modern Data Streaming Demo

This repository contains the demo used in the _Developing a stream processing application with Apache Kafka and Quarkus_ talk:

> Developing a stream processing application with Apache Kafka and Quarkus
The popularity of AI features like machine learning and data services has created a need to collect data from multiple sources, like customer-facing websites, edge devices, databases, etc. This has increased the need for high-performance stream processing.
In this talk, we will discuss different techniques for collecting data but mainly show how Quarkus, in combination with MicroProfile Reactive Messaging, lets you build Kafka-based event-driven architectures. Using a sample application, we will illustrate the concepts and common patterns and show how Quarkus makes the development of event-driven microservices smooth. Expect lots of live coding, showcasing live reload, Dev UI, continuous testing, etc.

* [Slides](https://drive.google.com/file/d/1lYT9nVr0gl5k97pRhYwDZLJx8ltxqmP4/view?usp=drive_link)

## The demo

The demo shows how to architect, implement, and deploy modern data streaming applications on Kubernetes (OpenShift in this case).

It contains:

- Temperature and camera sensors:
  These fake sensors exist in two flavors: behaving correctly or misbehaving.
  They are deployed as pods in Kubernetes
- A measure enrichment service:
  It receives the temperatures and the sensors' snapshots and associates a location based on the device id. It then produces these enriched temperatures and snapshots to 2 Kafka topics.
- An alert manager service:
  It consumes the enriched temperatures and snapshots and detects misbehavior. For temperature, it just detects a temperature that is not in a valid range and, in this case, sends an alert on a Kafka topic. For snapshots, it uses the prediction service to detect if the picture contains a rabbit (highly unexpected) and, in this case, sends an alert on another Kafka topic.
- A prediction service (picture analyzer):
  This service is a Python/TensorFlow program that takes a picture as input and returns the objects recognized in the picture.
- A dashboard:
  This Quarkus application exposes a web UI to present the alerts and number of messages.

## Building and Running

This repo uses [Just](https://github.com/casey/just) to automate most commands.
Check the [justfile](./justfile) to see all the _recipes_ or run `just -l` from the project root.

### Building and pushing the images

_IMPORTANT:_ The images are pushed on the `quay.io` image registry under the `devnation` organization. Please updates the `application.properties` files and the descriptors if you need to change the target:

Build and push the images with the following:

```
just build-and-push-images
```

### Deploying to Kubernetes

Once the images are built and pushed to a registry, you can deploy the demo using the following:

```
just deploy-to-kube
```

It will not create the sensors.
Create the temperature sensors with the following:

```
just create-thermometers
```

Create the misbehaving temperature sensors with the following:

```
just create-bad-thermometers
```

Create the camera sensors with the following:

```
just create-cameras
```

Create the misbehaving camera sensors with the following:

```
just create-bad-cameras
```

#### Deleting the sensors

You can delete all sensors with the following:

```
just kill-all-devices
```