#!/bin/bash

nodes=("a", "b", "c")
RANDOM=$$$(date +%s)
LOGFILE=testrolling.log

function log() {
    msg="$(date '+%F %H:%M:%S') $1"
    echo $msg
    echo $msg >> $LOGFILE
}

function logdebug() {
    msg="$(date '+%F %H:%M:%S') $1"
    echo $msg >> $LOGFILE
}

function run4lw() {
    exec 5<>/dev/tcp/$2/$3;echo $1 >&5;cat <&5 | egrep "Mode: "
}

function srvr() {
    run4lw srvr $1 $2 2> /dev/null
}

function verify() {
    j=1
    while [ $j -lt 60 ]
    do
        ok=0
        for i in "${nodes[@]}";
        do 
            status=$(srvr $i 2181)
            logdebug "$i: $status"
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

function restart() {
    node=$1
    log "Restarting : $node"
    ssh -q andor@$node <<ENDSSH
    cd zookeeper
    PIDFILE=./$node:2181/zookeeper_server.pid
    if [ -e "\$PIDFILE" ]
    then
        kill -9 \$(cat \$PIDFILE)
        rm \$PIDFILE
    else
        echo "Missing \$PIDFILE, not stopping respective server"
    fi
    sleep 1
    java $ZKCONF_START_ZKOPTS -cp ./*:. org.apache.zookeeper.server.quorum.QuorumPeerMain ./$node:2181/zoo.cfg > ./$node:2181/zoo.log 2>&1 &
    echo -n \$! > ./$node:2181/zookeeper_server.pid
ENDSSH
}

ok=0
result=$(verify)
log "Result = $result"
if [ $result -ne 0 ];
then
    ok=1
fi

while [ $ok -eq 0 ];
do
    selectednode=${nodes[$RANDOM % ${#nodes[@]} ]}
    restart $selectednode
    result=$(verify)
    log "Result = $result"
    if [ $result -ne 0 ];
    then
        ok=1
        break
    fi
done



