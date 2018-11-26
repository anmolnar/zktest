package org.andor.zoomba;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class ReconnectOnAuthFailedTask implements ZoombaTask, Watcher {
    private ZooKeeper zk;

    private void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    private void reconnectOnAuthFailed(WatchedEvent watchedEvent) {
        System.out.println("Thread count = " + ManagementFactory.getThreadMXBean().getThreadCount());
        System.out.println("Current thread group name = " + Thread.currentThread().getName());
        System.out.println("Current thread group count = " + Thread.currentThread().getThreadGroup().activeCount());

        if (watchedEvent.getState() == Watcher.Event.KeeperState.AuthFailed) {
            try {
                Thread.sleep(3000);
                zk.close();
                zk = new ZooKeeper("andor-kdc.vpc.cloudera.com", 15000, this);
                zk.addAuthInfo("sasl", "blahbediblah".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() throws IOException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("localhost", 15000, this);
        this.setZk(zk);

        System.in.read();

        zk.close();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        reconnectOnAuthFailed(watchedEvent);
    }
}
