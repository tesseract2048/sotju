#!/bin/bash
ENV=$1
PKG=so-node.jar
INSTALL_DIR=~/install-so-node

function usage() {
    echo "Usage: bash $0 <env>"
    exit 1
}

if [ $# -lt 1 ];then
    usage
fi

sh build.sh ${ENV}
mkdir -p ${INSTALL_DIR}
mkdir -p ${INSTALL_DIR}/logs
cp ../target/${PKG} ${INSTALL_DIR}/
cp node.sh ${INSTALL_DIR}/

echo 'installation performed.'
