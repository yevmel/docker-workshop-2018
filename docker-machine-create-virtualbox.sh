#!/usr/bin/env bash

docker-machine create \
    --driver virtualbox \
    --virtualbox-cpu-count 2 \
    --virtualbox-memory 2048 \
    $@

