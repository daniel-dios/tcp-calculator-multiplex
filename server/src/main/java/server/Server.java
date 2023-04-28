package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import server.encoder.AnswerEncoder;
import server.operation.OperationDecoder;

public class Server {

    public static final int READ_EXIT_CODE = -1;
    public static final int MAX_FROM_CLIENT = 4;
    private final ServerParameters params;
    private final AnswerEncoder answerEncoder;
    private final OperationDecoder operationDecoder;

    public Server(
            final ServerParameters params,
            final AnswerEncoder answerEncoder,
            final OperationDecoder operationDecoder
    ) {
        this.params = params;
        this.answerEncoder = answerEncoder;
        this.operationDecoder = operationDecoder;
    }

    public void startListeningBlocking() {
        tryToOpenSocket(params.getPort())
                .ifPresentOrElse(
                        this::acceptingConnections,
                        () -> System.out.println("Impossible to connect")
                );
        System.out.println("See you soon.");
    }

    private void acceptingConnections(final ServerSocket socket) {
        try {
            while (true) {
                System.out.println("Listening...");
                final Socket clientSocket = socket.accept();
                new Thread(() -> handleNewConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Problem with " + e.getMessage());
        }
    }

    private void handleNewConnection(
            final Socket clientSocket
    ) {
        final var accumulator = new Accumulator(0);
        final var logger = new ClientPrinter(clientSocket.getInetAddress(), clientSocket.getPort());
        logger.info("connection accepted!");
        try (
                final var in = clientSocket.getInputStream();
                final var out = clientSocket.getOutputStream()
        ) {
            final var buffer = new byte[MAX_FROM_CLIENT];
            while (in.read(buffer) != READ_EXIT_CODE) {

                final var decode = operationDecoder.decode(buffer);
                if (decode.isPresent()) {
                    final var operation = decode.get();
                    logger.info("Received the operation: " + operation.toReadableFormat());
                    final var solution = operation.solve();
                    if (solution.success) {
                        logger.info("Solved: " + operation.toReadableFormat() + " = " + solution);
                        try {
                            logger.info("Accumulator before adding solution is:" + accumulator.getValue());
                            accumulator.accumulate(solution.result);
                            logger.info("Accumulator after accumulation is: " + accumulator.getValue());
                            out.write(answerEncoder.encode(accumulator.getValue()));
                            logger.info("Answered: " + accumulator);
                        } catch (Accumulator.AccumulatorMax e) {
                            sendError(logger, out, accumulator.getValue(), "Accumulator can't increase with the operation result.");
                        } catch (Accumulator.AccumulatorMin e) {
                            sendError(logger, out, accumulator.getValue(), "Accumulator can't decrease with the operation result.");
                        }
                    } else {
                        logger.info("Problem solving: " + operation.toReadableFormat() + ", error: " + solution.reason);
                        sendError(logger, out, accumulator.getValue(), solution.reason);
                    }
                } else {
                    sendError(logger, out, accumulator.getValue(), "invalid input");
                }
            }
        } catch (IOException e) {
            logger.info("IOException with client reason:" + e.getMessage());
        } finally {
            logger.info("Connection closed with client.");
        }
    }

    private void sendError(
            final ClientPrinter logger,
            final OutputStream out,
            final long accumulatorValue,
            final String reason
    ) throws IOException {
        out.write(answerEncoder.encode(accumulatorValue, reason));
        logger.info("Answered accumulator: " + accumulatorValue + ", error: " + reason);
    }

    private Optional<ServerSocket> tryToOpenSocket(final int port) {
        try {
            return Optional.of(new ServerSocket(port));
        } catch (IOException e) {
            System.out.println("Could not open the socket on port: " + port + ", reason: " + e.getMessage());
            return Optional.empty();
        }
    }

    private static class ClientPrinter {
        private final String header;

        public ClientPrinter(final InetAddress inetAddress, final int port) {
            this.header = "[IP '" + inetAddress.getHostAddress() + "', Port '" + port + "'] ";
        }

        public void info(String message) {
            System.out.println(header + message);
        }
    }
}
