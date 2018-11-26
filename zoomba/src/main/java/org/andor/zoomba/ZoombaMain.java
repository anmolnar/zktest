package org.andor.zoomba;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ZoombaMain {

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, KeeperException {
        System.out.println("Zoomba starts");

        ZoombaTask task = new AddressResolution();
        task.run();
    }
}
