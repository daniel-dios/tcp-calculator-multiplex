package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.encoder.AnswerEncoder;
import server.operation.OperationDecoder;

public class Server {
    private static final String SEPARATOR = "------------------------------------------------------------------";
    private static final int MAX_FROM_CLIENT = 4;
    private final ServerParameters params;
    private final AnswerEncoder answerEncoder;
    private final OperationDecoder operationDecoder;
    private final Map<SocketChannel, Accumulator> accumulatorsTCP = new ConcurrentHashMap<>();
    private Accumulator accumulatorUDP = new Accumulator(0);

    public Server(
            final ServerParameters params,
            final AnswerEncoder answerEncoder,
            final OperationDecoder operationDecoder
    ) {
        this.params = params;
        this.answerEncoder = answerEncoder;
        this.operationDecoder = operationDecoder;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startListeningBlocking() {
        try {
            final var selector = openSelector();

            startTCP(selector);
            startUDP(selector);

            System.out.println("Listening forever...");
            while (true) {
                selector.select();

                var selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    System.out.println(SEPARATOR);
                    var key = selectedKeys.next();
                    selectedKeys.remove();

                    final var channel = key.channel();
                    if (key.isAcceptable()) {
                        if (channel instanceof ServerSocketChannel) {
                            accept(selector, (ServerSocketChannel) channel);
                        } else {
                            System.out.println("Acceptable key is not instance of ServerSocketChannel");
                        }
                    } else if (key.isReadable()) {
                        if (channel instanceof SocketChannel) {
                            handleTCP((SocketChannel) channel);
                        } else if (channel instanceof DatagramChannel) {
                            handleUDP((DatagramChannel) channel);
                        }
                    }
                }
            }
        } catch (SelectorException ex) {
            System.out.println("Problem opening selector " + ex.getMessage());
        } catch (TCPException ex) {
            System.out.println("Problem opening TCP " + ex.getMessage());
        } catch (UDPException ex) {
            System.out.println("Problem opening UDP " + ex.getMessage());
        } catch (IOException e) {
            System.out.println("Problem selecting " + e.getMessage());
        }
    }

    private static Selector openSelector() throws SelectorException {
        try {
            return Selector.open();
        } catch (IOException e) {
            throw new SelectorException(e.getMessage());
        }
    }


    private void startTCP(final Selector selector) throws TCPException {
        try {
            final var tcpServer = ServerSocketChannel.open();
            tcpServer.bind(new InetSocketAddress(params.getPort()));
            tcpServer.configureBlocking(false);
            tcpServer.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("TCP ready.");
        } catch (Exception ex) {
            throw new TCPException(ex.getMessage());
        }
    }

    private void startUDP(final Selector selector) throws UDPException {
        try {
            final var udpServer = DatagramChannel.open();
            udpServer.bind(new InetSocketAddress(params.getPort()));
            udpServer.configureBlocking(false);
            udpServer.register(selector, SelectionKey.OP_READ);
            System.out.println("UDP ready.");
        } catch (Exception ex) {
            throw new UDPException(ex.getMessage());
        }
    }

    private void accept(
            final Selector selector,
            final ServerSocketChannel channel
    ) {
        try {
            var clientChannel = channel.accept();
            System.out.println("[TCP]" + clientChannel.getRemoteAddress() + " accepted!");
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            accumulatorsTCP.put(clientChannel, new Accumulator(0));
            System.out.println("[TCP]" + clientChannel.getRemoteAddress() + " accumulator set to 0.");
        } catch (Exception e) {
            System.out.println("[TCP] Problem with " + e.getMessage());
        }
    }

    private void handleUDP(final DatagramChannel udpChannel) {
        try {
            final var request = ByteBuffer.allocate(MAX_FROM_CLIENT);
            final var clientAddress = (InetSocketAddress) udpChannel.receive(request);
            if (clientAddress != null) {
                final var logger = new Logger(clientAddress);
                logger.info("Received a message of:" + request.array().length + "bytes.");
                final var answer = handleUDPOperation(request.array(), logger);
                udpChannel.send(ByteBuffer.wrap(answer), clientAddress);
                logger.info("Replied with " + answer.length + "bytes: " + Arrays.toString(answer));
            }
        } catch (Exception ex) {
            System.out.println("UDP Problem with " + ex.getMessage());
        }
    }

    private void handleTCP(final SocketChannel clientChannel) {
        final var logger = new Logger(clientChannel);
        try {
            final var request = ByteBuffer.allocate(MAX_FROM_CLIENT);
            final var bytesRead = clientChannel.read(request);

            if (bytesRead == -1) {
                accumulatorsTCP.remove(clientChannel);
                clientChannel.close();
                return;
            }

            logger.info("Received a message of:" + request.array().length + "bytes.");
            final var answer = handleTCPOperation(request.array(), clientChannel, logger);
            clientChannel.write(ByteBuffer.wrap(answer));
            logger.info("Replied with " + answer.length + "bytes: " + Arrays.toString(answer));
        } catch (Exception exception) {
            logger.info("TCP Problem with " + exception.getMessage());
        }
    }

    private byte[] handleTCPOperation(final byte[] array, SocketChannel socketChannel, final Logger logger) {
        System.out.println(SEPARATOR);
        final var accumulator = accumulatorsTCP.get(socketChannel);
        logger.info("Accumulator is: " + accumulator);
        final var decode = operationDecoder.decode(array);
        if (decode.isPresent()) {
            final var operation = decode.get();
            logger.info("Operation received is: " + operation.toReadableFormat());
            final var solve = operation.solve();
            if (solve.success) {
                logger.info("Solved Operation: " + operation.toReadableFormat() + "=" + solve.result);
                try {
                    accumulator.accumulate(solve.result);
                    accumulatorsTCP.put(socketChannel, accumulator);
                    logger.info("New accumulator is: " + accumulator.getValue() + ", stored: " + accumulatorsTCP.get(socketChannel));
                    return answerEncoder.encode(accumulator.getValue());
                } catch (Accumulator.AccumulatorMax e) {
                    logger.info("Accumulator cant increase due to limit, replying with old accumulator: " + accumulator.getValue());
                    return answerEncoder.encodeWithError(accumulator.getValue(), "Accumulator can't increase with the operation result.");
                } catch (Accumulator.AccumulatorMin e) {
                    logger.info("Accumulator cant decrease due to limit, replying with old accumulator: " + accumulator.getValue());
                    return answerEncoder.encodeWithError(accumulator.getValue(), "Accumulator can't decrease with the operation result.");
                }
            } else {
                logger.info("Operation can't be solved: " + solve.reason + "replying with old accumulator: " + accumulator.getValue());
                return answerEncoder.encodeWithError(accumulator.getValue(), solve.reason);
            }
        } else {
            logger.info("Input invalid, replying with old accumulator: " + accumulator.getValue());
            return answerEncoder.encodeWithError(accumulator.getValue(), "invalid input");
        }
    }

    private byte[] handleUDPOperation(final byte[] array, final Logger logger) {
        final var accumulator = this.accumulatorUDP;
        logger.info("Accumulator before operation: " + accumulator);
        final var decode = operationDecoder.decode(array);
        if (decode.isPresent()) {
            final var operation = decode.get();
            logger.info("Operation received is: " + operation.toReadableFormat());
            final var solve = operation.solve();
            if (solve.success) {
                logger.info("Solved Operation: " + operation.toReadableFormat() + "=" + solve.result);
                try {
                    accumulator.accumulate(solve.result);
                    this.accumulatorUDP = accumulator;
                    logger.info("New accumulator is: " + accumulator.getValue() + ", stored: " + this.accumulatorUDP);
                    return answerEncoder.encode(accumulator.getValue());
                } catch (Accumulator.AccumulatorMax e) {
                    logger.info("Accumulator cant increase due to limit, replying with old accumulator: " + accumulator.getValue());
                    return answerEncoder.encodeWithError(accumulator.getValue(), "Accumulator can't increase with the operation result.");
                } catch (Accumulator.AccumulatorMin e) {
                    logger.info("Accumulator cant decrease due to limit, replying with old accumulator: " + accumulator.getValue());
                    return answerEncoder.encodeWithError(accumulator.getValue(), "Accumulator can't decrease with the operation result.");
                }
            } else {
                logger.info("Operation can't be solved: " + solve.reason + "replying with old accumulator: " + accumulator.getValue());
                return answerEncoder.encodeWithError(accumulator.getValue(), solve.reason);
            }
        } else {
            logger.info("Input invalid, replying with old accumulator: " + accumulator.getValue());
            return answerEncoder.encodeWithError(accumulator.getValue(), "invalid input");
        }
    }

    private static class UDPException extends Throwable {
        public UDPException(final String message) {
            super(message);
        }
    }

    private static class TCPException extends Throwable {
        public TCPException(final String message) {
            super(message);
        }
    }

    private static class SelectorException extends Throwable {
        public SelectorException(final String message) {
            super(message);
        }
    }
}
