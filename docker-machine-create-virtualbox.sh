#!/usr/bin/env bash

docker-machine create \
    --driver virtualbox \
    --virtualbox-cpu-count 2 \
    --virtualbox-memory 2048 \
    --engine-insecure-registry $(docker-machine ip infra):5000 \
    $@

