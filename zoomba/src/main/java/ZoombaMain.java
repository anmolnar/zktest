import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ZoombaMain {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        System.out.println("Zoomba starts");

        SSLContext context = SSLContext.getDefault();

        SSLSocketFactory socketFactory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket();


        System.out.println("Provider = " + context.getProvider());
        System.out.println("Protocol = " + context.getProtocol());
        System.out.println("Enabled protocols = " + Arrays.toString(socket.getEnabledProtocols()));
    }
}
