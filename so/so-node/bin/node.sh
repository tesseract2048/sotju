#! /bin/bash
HOME_DIR=~/install-so-node
PKG_NAME=so-node.jar

LOG_DIR=logs
LOG_FILE=node.log
OUT_FILE=node.out
GC_FILE=node.gc

JAVA_OPTS="-Xms1024m -Xmx2048m -Dso.log.dir=${LOG_DIR} -Dso.log.file=${LOG_FILE} -Dso.root.logger=INFO,DRFA -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:${LOG_DIR}/${GC_FILE}"

MAINCLASS="org.tju.so.node.SearchNode"

source ~/.bashrc

function usage() {
    echo "Usage: $0 [start / stop]"
}

if [ $# -lt 1 ]; then
    usage
    exit 1
fi

function stop() {
    PROCESSES=`ps x | grep $MAINCLASS | grep java | awk {'print $1'}`
    for PRC in $PROCESSES
    do
        kill $PRC
        echo kill process: $PRC
    done
}

function start() {
    cd ${HOME_DIR}
    CLASSPATH=${CLASSPATH}:./${PKG_NAME};
    export CLASSPATH

    nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} ${MAINCLASS} >${LOG_DIR}/${OUT_FILE} 2>${LOG_DIR}/${OUT_FILE} &

    sleep 3
    # show some logs by tail
    tail -n 10 ${LOG_DIR}/${LOG_FILE}
}

if [ $1 == "start" ];then
    start 
fi

if [ $1 == "stop" ];then
    stop 
fi
