#!/bin/bash
NAME=quay.io/cescoffi/devnation-2023-picture-analyzer
docker buildx build --platform=amd64 -t ${NAME} .
docker push ${NAME}