#!/usr/bin/env bash

docker-machine create \
    --driver virtualbox \
    --virtualbox-cpu-count 1 \
    --virtualbox-memory 1024 \
    infra

