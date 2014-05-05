#!/bin/bash
ENV=$1

function usage() {
    echo "Usage: bash $0 <env>"
    exit 1
}

if [ $# -lt 1 ];then
    usage
fi

cd ..
mvn clean package -Dmaven.test.skip=true -Denv=${ENV}

echo 'build succeed.'