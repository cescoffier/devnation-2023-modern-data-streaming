#!/bin/bash
NAME=quay.io/devnation/devnation-2023-picture-analyzer
docker buildx build --platform=linux/amd64 -t ${NAME} .
docker push ${NAME}