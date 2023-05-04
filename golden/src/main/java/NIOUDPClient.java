import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class NIOUDPClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        final var serverAddress = new InetSocketAddress(HOST, PORT);

        final var client = DatagramChannel.open();
        client.configureBlocking(false);

        final var buffer = ByteBuffer.allocate(10);
        final var scanner = new Scanner(System.in);

        for (int i = 0; i < 100; i++) {
            scanner.nextLine();
            buffer.put((byte) i);
            buffer.flip();
            client.send(buffer, serverAddress);

            buffer.clear();
            final var responseAddress = (InetSocketAddress) client.receive(buffer);
            if (responseAddress != null) {
                buffer.flip();
                final var responseData = new byte[buffer.remaining()];
                buffer.get(responseData);
                final var response = new String(responseData);

                System.out.println("Respuesta del servidor para el dígito " + i + ": " + response);
            }

            buffer.clear();
        }

        client.close();
    }
}
