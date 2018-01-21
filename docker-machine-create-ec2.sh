#!/usr/bin/env bash

#
# keep in mind, you need a VPC with ip range of 172.31.0.0/16 (used by docker by default)
#

NODE_NAME=${1:-ec2-docker-2018}

# depend on configuration of your VPC
SUBNE_ID=subnet-79614230
VPC_ID=vpc-f0527b97
EC2_ZONE=b

docker-machine create \
    --driver amazonec2 \
    --amazonec2-region eu-west-1 \
    --amazonec2-subnet-id $SUBNE_ID \
    --amazonec2-vpc-id $VPC_ID \
    --amazonec2-zone $EC2_ZONE \
    --amazonec2-open-port 8080 \
    --amazonec2-open-port 80 \
    $@

