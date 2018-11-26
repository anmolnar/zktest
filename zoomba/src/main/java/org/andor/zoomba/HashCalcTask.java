package org.andor.zoomba;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class HashCalcTask implements ZoombaTask, Watcher {
    @Override
    public void run() throws IOException, NoSuchAlgorithmException, InterruptedException, KeeperException {

        ZooKeeper zk1 = null;
        try {
            zk1 = new ZooKeeper("localhost:2181", 30000, this);
            System.out.println("Zk clients connected");

            String path = "/andor5";

            //createNodeWithAcl(zk1, path);
            addSecondAcl(zk1, path);
            getNodeWithAcl(zk1, path);


        } finally {
            if (zk1 != null) {
                zk1.close();
                System.out.println("Zk clients disconnected");
            }
        }
    }

    private void addSecondAcl(ZooKeeper zk1, String path) throws KeeperException, InterruptedException, NoSuchAlgorithmException {
        zk1.addAuthInfo("digest", "user2:password2".getBytes());

        Stat stat = zk1.exists(path, false);
        List<ACL> acl1 = zk1.getACL(path, stat);

        String s2 = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest("user:password".getBytes()));
        ACL acl2 = new ACL(ZooDefs.Perms.ALL, new Id("digest", "user:" + s2));
        acl1.add(acl2);
        zk1.setACL(path, acl1, stat.getVersion() + 1);
    }

    private void getNodeWithAcl(ZooKeeper zk1, String path) throws InterruptedException {
        Stat stat;
        try {
            stat = zk1.exists(path, false);
            System.out.println("Stat = " + stat.toString());
            zk1.addAuthInfo("digest", "user:password".getBytes());
            byte[] data = zk1.getData(path, false, stat);
            System.out.println("Data = " + new String(data));
        } catch (KeeperException e) {
            System.out.println("KeeperException = " + e.getMessage());
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
    }

    private void createNodeWithAcl(ZooKeeper zk1, String path) throws NoSuchAlgorithmException, KeeperException, InterruptedException {
        String s = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest("user:password".getBytes()));
        ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("digest", "user:" + s));
        zk1.create(path, "test".getBytes(), Arrays.asList(acl), CreateMode.PERSISTENT);
    }
}
