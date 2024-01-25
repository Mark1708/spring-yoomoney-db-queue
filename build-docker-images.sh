#!/bin/bash
cd test-app/ && gradle nativeCompile
docker build -t ghcr.io/mark1708/test-app .

cd ../consumer-app/ && gradle nativeCompile
docker build -t ghcr.io/mark1708/consumer-app .

cd ../producer-app/ && gradle nativeCompile
docker build -t ghcr.io/mark1708/producer-app .