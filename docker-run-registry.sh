#!/usr/bin/env bash

docker run --detach --name registry --publish 5000:5000 registry:2

