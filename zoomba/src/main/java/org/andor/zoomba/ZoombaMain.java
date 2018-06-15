package org.andor.zoomba;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ZoombaMain {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        System.out.println("Zoomba starts");

        String javaVersion = System.getProperty("java.version");
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
}
