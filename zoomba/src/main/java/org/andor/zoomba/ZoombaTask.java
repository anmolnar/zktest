package org.andor.zoomba;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ZoombaTask {
    void run() throws IOException, NoSuchAlgorithmException, InterruptedException, KeeperException;
}
