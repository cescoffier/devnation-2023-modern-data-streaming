#!/bin/bash
kubectl delete pods -l app.kubernetes.io/part-of=sensors
