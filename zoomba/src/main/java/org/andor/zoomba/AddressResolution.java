package org.andor.zoomba;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AddressResolution implements ZoombaTask {
    @Override
    public void run() throws IOException, NoSuchAlgorithmException, InterruptedException, KeeperException {
        InetSocketAddress addr1 = InetSocketAddress.createUnresolved("pingus.hu", 80);

        System.out.println("InetSocketAddress = " + addr1);
        System.out.println("Unresolved = " + addr1.isUnresolved());

        InetAddress a1 = addr1.getAddress();

        System.out.println("InetSocketAddress.HostName = " + addr1.getHostName());

        InetAddress[] resolvedAddresses = InetAddress.getAllByName(addr1.getHostName());

        System.out.println(Arrays.toString(resolvedAddresses));

        InetSocketAddress addr2 = new InetSocketAddress(resolvedAddresses[0],
                addr1.getPort()
        );

        System.out.println("addr2 = " + addr2.toString());
        System.out.println("Unresolved = " + addr2.isUnresolved());

        System.out.println("hostname = " + addr2.getAddress().getCanonicalHostName());
    }
}
