import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class NIOServer {

    private final int size = 1;
    private final int port = 8080;

    public NIOServer() {

    }


    private final Map<SocketChannel, Integer> accumulators = new HashMap<>();
    private int udpAccumulator = 0;

    public static void main(String[] args) {
        new NIOServer().start();
    }

    private void start() {
        try {
            final var selector = Selector.open();

            final var tcpServer = ServerSocketChannel.open();
            tcpServer.bind(new InetSocketAddress(port));
            tcpServer.configureBlocking(false);
            tcpServer.register(selector, SelectionKey.OP_ACCEPT);

            final var udpServer = DatagramChannel.open();
            udpServer.bind(new InetSocketAddress(port));
            udpServer.configureBlocking(false);
            udpServer.register(selector, SelectionKey.OP_READ);

            System.out.println("Listening forever...");
            while (true) {
                selector.select();

                var selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    var key = selectedKeys.next();
                    selectedKeys.remove();

                    final var channel = key.channel();
                    if (key.isAcceptable()) {
                        accept(selector, (ServerSocketChannel) channel);
                    } else if (key.isReadable()) {
                        handleConnection(channel);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Problem with " + ex);
        }
    }

    private void accept(final Selector selector, final ServerSocketChannel channel) {
        try {
            System.out.println("New acceptable key!");
            var clientChannel = channel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            accumulators.put(clientChannel, 0);
        } catch (Exception e) {
            System.out.println("Problem with " + e.getMessage());
        }
    }

    private void handleConnection(final SelectableChannel channel) {
        if (channel instanceof SocketChannel) {
            System.out.println("Handling TCP");
            handleTCP((SocketChannel) channel);
        } else if (channel instanceof DatagramChannel) {
            System.out.println("Handling UDP");
            try {
                handleUDP((DatagramChannel) channel);
            } catch (IOException e) {
                System.out.println("Problem with " + e.getMessage());
            }
        }
    }

    private void handleUDP(final DatagramChannel udpChannel) throws IOException {
        var buffer = ByteBuffer.allocate(size);
        var clientAddress = (InetSocketAddress) udpChannel.receive(buffer);

        if (clientAddress != null) {
            buffer.flip();
            final byte b = buffer.get();
            System.out.println("Mensaje UDP recibido: " + b);
            System.out.println("Received: " + b + "for UDP clients the accumulator was: " + udpAccumulator + " and now: " + (udpAccumulator + b));
            udpAccumulator += b;
            buffer.clear();
            buffer.put((byte) udpAccumulator);
            buffer.flip();

            udpChannel.send(buffer, clientAddress);
        }
    }

    private void handleTCP(final SocketChannel clientChannel) {
        try {

            var buffer = ByteBuffer.allocate(size);
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                accumulators.remove(clientChannel);
                clientChannel.close();
            } else {
                buffer.flip();
                final byte b = buffer.get();
                final var integer = accumulators.get(clientChannel);
                System.out.println("received: " + b + "for this client the accumulator was: " + integer + " and now: " + (integer + b));
                int accumulator = integer + b;
                accumulators.put(clientChannel, accumulator);

                buffer.clear();
                buffer.put((byte) accumulator);
                buffer.flip();

                clientChannel.write(buffer);
            }
        } catch (Exception exception) {
            System.out.println("Problem with " + exception.getMessage());
        }
    }
}