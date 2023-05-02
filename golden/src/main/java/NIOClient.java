import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);

        // Crear el cliente y establecer conexión con el servidor
        SocketChannel client = SocketChannel.open(serverAddress);

        ByteBuffer buffer = ByteBuffer.allocate(10);
        final Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 10; i++) {
            final String s = scanner.nextLine();
            System.out.println("introduced: " + s);
            // Enviar el dígito al servidor
            buffer.put((byte) i);
            buffer.flip();
            client.write(buffer);

            // Leer la respuesta del servidor
            buffer.clear();
            client.read(buffer);
            buffer.flip();
            int response = buffer.get();

            // Imprimir la respuesta del servidor
            System.out.println("Respuesta del servidor para el dígito " + (i) + ": " + response);

            buffer.clear();
        }

        // Cerrar la conexión
        client.close();
    }
}
