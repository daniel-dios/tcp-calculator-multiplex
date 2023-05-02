import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        final var serverAddress = new InetSocketAddress(HOST, PORT);

        final var client = SocketChannel.open(serverAddress);

        final var buffer = ByteBuffer.allocate(10);
        final var scanner = new Scanner(System.in);

        for (int i = 0; i < 10; i++) {
            scanner.nextLine();
            buffer.put((byte) i);
            buffer.flip();
            client.write(buffer);

            buffer.clear();
            client.read(buffer);
            buffer.flip();
            int response = buffer.get();

            System.out.println("Respuesta del servidor para el dígito " + (i) + ": " + response);

            buffer.clear();
        }

        client.close();
    }
}
