package org.andor.zoomba;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

public class AddressEqualityTask implements ZoombaTask {
    @Override
    public void run() throws IOException, NoSuchAlgorithmException, InterruptedException {
        InetSocketAddress first = new InetSocketAddress("www.apache.org", 80);
        InetSocketAddress second = new InetSocketAddress("95.216.24.32", 80);

        InetSocketAddress third = InetSocketAddress.createUnresolved("www.apache.org", 80);

        System.out.println("=== Java ===");
        System.out.printf("%s equals %s = %s\n", first, second, first.equals(second));
        System.out.printf("%s equals %s = %s\n", second, third, second.equals(third));
        System.out.printf("%s equals %s = %s\n", first, third, first.equals(third));
        System.out.printf("%s equals %s = %s\n", third, first, third.equals(first));

        System.out.println("=== myequals ===");
        System.out.printf("%s equals %s = %s\n", first, second, myequals(first, second));
        System.out.printf("%s equals %s = %s\n", second, third, myequals(second, third));
        System.out.printf("%s equals %s = %s\n", first, third, myequals(first, third));
        System.out.printf("%s equals %s = %s\n", third, first, myequals(third, first));
    }

    boolean myequals(InetSocketAddress addr, InetSocketAddress myServer) {
        if (addr.getPort() == myServer.getPort()
                && ((addr.getAddress() != null
                && myServer.getAddress() != null && addr
                .getAddress().equals(myServer.getAddress())) || addr
                .getHostString().equals(myServer.getHostString()))) {
            return true;
        }
        return false;
    }
}
