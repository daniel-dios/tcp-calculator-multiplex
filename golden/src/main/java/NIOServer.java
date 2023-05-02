import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NIOServer {

    private final int size = 1;
    private final int port = 8080;
    private final Map<SocketChannel, Integer> accumulators = new HashMap<>();
    private final AtomicInteger udpAccumulator = new AtomicInteger(0);

    public NIOServer() {
    }

    public static void main(String[] args) {
        new NIOServer().start();
    }

    private void start() {
        try {
            final var selector = Selector.open();

            startTCP(selector);
            startUDP(selector);

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
                        if (channel instanceof SocketChannel) {
                            handleTCP((SocketChannel) channel);
                        } else if (channel instanceof DatagramChannel) {
                            handleUDP((DatagramChannel) channel);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Problem with " + ex);
        }
    }

    private void startTCP(final Selector selector) throws IOException {
        final var tcpServer = ServerSocketChannel.open();
        tcpServer.bind(new InetSocketAddress(port));
        tcpServer.configureBlocking(false);
        tcpServer.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("TCP ready.");
    }

    private void startUDP(final Selector selector) throws IOException {
        final var udpServer = DatagramChannel.open();
        udpServer.bind(new InetSocketAddress(port));
        udpServer.configureBlocking(false);
        udpServer.register(selector, SelectionKey.OP_READ);
        System.out.println("UDP ready.");
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

    private void handleUDP(final DatagramChannel udpChannel) {
        try {
            System.out.println("Handling UDP");
            var buffer = ByteBuffer.allocate(size);
            var clientAddress = (InetSocketAddress) udpChannel.receive(buffer);

            if (clientAddress != null) {
                buffer.flip();
                final byte b = buffer.get();
                System.out.println("Mensaje UDP recibido: " + b);
                System.out.println("Received: " + b + "for UDP clients the accumulator was: " + udpAccumulator.get() + " and now: " + udpAccumulator.addAndGet(b));
                buffer.clear();
                buffer.put((byte) udpAccumulator.get());
                buffer.flip();
                udpChannel.send(buffer, clientAddress);
            }
        } catch (Exception ex) {
            System.out.println("UDP Problem with " + ex.getMessage());
        }
    }

    private void handleTCP(final SocketChannel clientChannel) {
        try {
            System.out.println("Handling TCP");
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
            System.out.println("TCP Problem with " + exception.getMessage());
        }
    }
}