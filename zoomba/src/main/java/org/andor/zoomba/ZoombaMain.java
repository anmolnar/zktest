package org.andor.zoomba;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ZoombaMain implements Watcher {
    private ZooKeeper zk;

    private void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        System.out.println("Zoomba starts");

        ZoombaMain zoomba = new ZoombaMain();
        ZooKeeper zk = new ZooKeeper("andor-kdc.vpc.cloudera.com", 15000, zoomba);
        zoomba.setZk(zk);

        zk.addAuthInfo("sasl", "blahbediblah".getBytes());

        System.in.read();
    }

    private static void versions() throws NoSuchAlgorithmException, IOException {
        String javaVersion = System.getProperty("java.specification.version");
        System.out.println("Java version = " + javaVersion);

        String[] versionParts = javaVersion.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        int minorVersion = Integer.parseInt(versionParts[1]);

        if (majorVersion >= 9) {
            System.out.println("This is at least Java 9");
        } else {
            System.out.println("This is before Java 9");
        }

        SSLContext context = SSLContext.getDefault();

        SSLSocketFactory socketFactory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket();


        System.out.println("Provider = " + context.getProvider());
        System.out.println("Protocol = " + context.getProtocol());
        System.out.println("Enabled protocols = " + Arrays.toString(socket.getEnabledProtocols()));
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);

        System.out.println("Thread count = " + ManagementFactory.getThreadMXBean().getThreadCount());
        System.out.println("Current thread group name = " + Thread.currentThread().getName());
        System.out.println("Current thread group count = " + Thread.currentThread().getThreadGroup().activeCount());



    }

    private void reconnectOnAuthFailed(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.AuthFailed) {
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
}
