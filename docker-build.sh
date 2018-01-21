#!/usr/bin/env bash

# name of the image,
# default is current directory name
NAME=${1:-${PWD##*/}}

# host/ip of machine running the registry
REGISTRY=$(docker-machine ip infra):5000

docker build -t $NAME .
docker tag $NAME $REGISTRY/$NAME
docker push $REGISTRY/$NAME

