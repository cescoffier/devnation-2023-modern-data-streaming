#!/bin/bash
NAME=quay.io/devnation/devnation-2023-picture-analyzer
docker build -t ${NAME} .
echo "Run the container using \`docker run docker run -i --rm -p 8085:8080 --cpus=1 ${NAME}\`"
echo "Warning... it takes a HUGE amount of memory!"