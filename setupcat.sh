#!/bin/bash

for i in $@;
do
    echo "Setting up server $i ..."
    ssh root@$i <<ENDSSH
    yum -y install java
    adduser andor
    mkdir /home/andor/.ssh
    chown andor:andor /home/andor/.ssh
    chmod 700 /home/andor/.ssh
ENDSSH
    scp ~/.ssh/id_rsa.pub root@$i:/home/andor/.ssh/authorized_keys
    ssh root@$i <<ENDSSH
    chown andor:andor /home/andor/.ssh/authorized_keys
    chmod 600 /home/andor/.ssh/authorized_keys
ENDSSH
done

