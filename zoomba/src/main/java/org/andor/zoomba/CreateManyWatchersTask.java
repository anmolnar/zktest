package org.andor.zoomba;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CreateManyWatchersTask implements ZoombaTask, Watcher {
    @Override
    public void run() throws IOException, NoSuchAlgorithmException, InterruptedException, KeeperException {
        ZooKeeper zk1 = null;
        ZooKeeper zk2 = null;
        try {
            zk1 = new ZooKeeper("localhost:2181", 30000, this);
            zk2 = new ZooKeeper("localhost:2182", 30000, this);
            System.out.println("Zk clients connected");

            // 110 character base path
            String pathBase = "/long-path-000000000-111111111-222222222-333333333-444444444-"
                    + "555555555-666666666-777777777-888888888-999999999-1";

            zk1.create(pathBase, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            // Create 10,000 nodes. This should ensure the length of our
            // watches set below exceeds 1MB.
            List<String> paths = new ArrayList<String>();
            for (int i = 0; i < 100000; i++) {
                String path = zk1.create(pathBase + "/ch-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT_SEQUENTIAL);
                paths.add(path);
            }

            System.out.printf("Created %d nodes.\n", paths.size());
            Watcher childWatcher = new MyWatcher();

            // Set a combination of child/exists/data watches
            int i = 0;
            for (String path : paths) {
                if (i % 3 == 0) {
                    zk2.getChildren(path, childWatcher);
                } else if (i % 3 == 1) {
                    zk2.exists(path + "/foo", childWatcher);
                } else if (i % 3 == 2) {
                    zk2.getData(path, childWatcher, null);
                }

                i++;
            }

            System.out.println("Watches are set.");

//            long sessionId = zk2.getSessionId();
//            byte[] sessionPassword = zk2.getSessionPasswd();
//
//            zk2.close();
//
//            zk2 = new ZooKeeper("localhost:2182", 30000, this, sessionId, sessionPassword);

//            System.out.println("zk2 reconnected.");





            System.in.read();
            System.out.println("Done.");
        } finally {
            if (zk2 != null) {
                zk2.close();
            }
            if (zk1 != null) {
                zk1.close();
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
    }

    private class MyWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            System.out.println("ChildWatch: " + event.toString());
        }
    }
}
