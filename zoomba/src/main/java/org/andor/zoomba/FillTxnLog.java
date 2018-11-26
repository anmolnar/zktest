package org.andor.zoomba;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class FillTxnLog implements ZoombaTask, Watcher {
    @Override
    public void run() throws IOException, NoSuchAlgorithmException, InterruptedException, KeeperException {

        ZooKeeper zk1 = null;
        try {
            zk1 = new ZooKeeper("localhost:2181", 30000, this);
            System.out.println("Zk clients connected");

            // Create parent node
            String path = "/andorFiller";
            Stat stat = zk1.exists(path, false);
            if (stat == null) {
                zk1.create(path, "andorFiller".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                System.out.println("Parent node created");
            }

            // Create children
            int num = 100000;
            for (int i = 0; i < num; i++) {
                zk1.create(path + "/data-", "data".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            }

            System.out.println("Number of data nodes created = " + num);
        } finally {
            if (zk1 != null) {
                zk1.close();
                System.out.println("Zk clients disconnected");
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    }
}
