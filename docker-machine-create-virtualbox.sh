#!/usr/bin/env bash


docker-machine create \
    --driver virtualbox \
    --virtualbox-cpu-count 2 \
    --virtualbox-memory 8192 \
    --engine-insecure-registry $(docker-machine ip infra):5000 \
    $@

