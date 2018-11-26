#!/bin/bash

zk_dir=$(pwd)
docker_img="phunt/zk-docker-devenv.centos.7.4.1708"
zk_data_dir="./Andors-MacBook-Pro.local:218"

function start() {
    i=$1
    ip=$((1+$i))
    docker run --net zknet --ip 172.18.0.$ip -p 218$i:218$i -d -v $zk_dir:/app -w /app $docker_img java -cp ./*:. org.apache.zookeeper.server.quorum.QuorumPeerMain $zk_data_dir$i/zoo.cfg | tee $zk_data_dir$i/docker_id
}

function stop() {
    i=$1
    dockeridfile=$zk_data_dir$i/docker_id
    if [ -e "$dockeridfile" ]
    then
        docker stop $(cat $dockeridfile)
    else
        echo "Missing $dockeridfile, not stopping respective server"
    fi
}

function create_net() {
    docker network create --subnet=172.18.0.0/16 zknet
}

function run4lw() {
    exec 5<>/dev/tcp/$2/$3;echo $1 >&5;cat <&5 | egrep "Mode: "
}

function srvr() {
    run4lw srvr $1 $2 2> /dev/null
}

function verify() {
    j=0
    while [ $j -lt 10 ]
    do
        ok=0
        for i in $(seq 1 3);
        do
            port="218$i"
            status=$(srvr localhost $port)
            echo "$i: $status" > /dev/stderr
            if [ "$status" == "" ];
            then
                ok=1
                break
            fi
        done
        if [ $ok -eq 0 ];
        then
            break
        fi
        j=$[$j+1]
        sleep 1
    done
    echo $ok
}

case $1 in
start)  
    if [ "x$2" == "x" ]
    then
        start 1
        start 2
        start 3
    else
        start $2
    fi
    ;;
stop)
    if [ "x$2" == "x" ]
    then
        stop 1
        stop 2
        stop 3
    else
        stop $2
    fi
    ;;
verify)
    exitcode=$(verify)
    exit $exitcode
    ;;
*)
    echo "Usage: ./rundocker (start|stop|verify) (1..3)"
    ;;
esac


