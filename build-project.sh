#!/bin/sh
docker run --rm -v "$PWD":/home/gradle/project -w /home/gradle/project gradle gradle build
