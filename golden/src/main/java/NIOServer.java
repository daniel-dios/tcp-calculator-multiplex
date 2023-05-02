import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NIOServer {

    private static final int BUFFER_SIZE = 1;
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        // Crear el servidor TCP
        ServerSocketChannel tcpServer = ServerSocketChannel.open();
        tcpServer.bind(new InetSocketAddress(PORT));
        tcpServer.configureBlocking(false);
        tcpServer.register(selector, SelectionKey.OP_ACCEPT);

        // Crear el servidor UDP
        DatagramChannel udpServer = DatagramChannel.open();
        udpServer.bind(new InetSocketAddress(PORT));
        udpServer.configureBlocking(false);
        udpServer.register(selector, SelectionKey.OP_READ);

        Map<SocketChannel, Integer> accumulators = new HashMap<>();

        while (true) {
            selector.select();

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    accumulators.put(clientChannel, 0);
                } else if (key.isReadable()) {
                    if (key.channel() instanceof SocketChannel) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

                        int bytesRead = clientChannel.read(buffer);
                        if (bytesRead == -1) {
                            accumulators.remove(clientChannel);
                            clientChannel.close();
                        } else {
                            buffer.flip();
                            final byte b = buffer.get();
                            final var integer = accumulators.get(clientChannel);
                            System.out.println("received: " + b + "for this client the accumulator is: " + integer + " and now: " + (integer + b));
                            int accumulator = integer + b;
                            accumulators.put(clientChannel, accumulator);

                            buffer.clear();
                            buffer.put((byte) accumulator);
                            buffer.flip();

                            clientChannel.write(buffer);
                        }
                    } else if (key.channel() instanceof DatagramChannel) {
                        DatagramChannel udpChannel = (DatagramChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        InetSocketAddress clientAddress = (InetSocketAddress) udpChannel.receive(buffer);

                        if (clientAddress != null) {
                            buffer.flip();
                            System.out.println("Mensaje UDP recibido: " + buffer.get());

                            buffer.clear();
                            buffer.put("FOO".getBytes());
                            buffer.flip();

                            udpChannel.send(buffer, clientAddress);
                        }
                    }
                }
            }
        }
    }
}