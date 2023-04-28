package client;

import client.answer.AnswerDecoder;
import client.operation.OperationReader;
import client.operation.Symbol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class Client {

    public static final String SEPARATOR = "------------------------------------------------------------------";
    public static final Duration TIMEOUT_FOR_ESTABLISH = Duration.ofSeconds(15);
    private final ClientParameters p;
    private final AnswerDecoder answerDecoder;
    private final OperationReader operationReader;

    public Client(
            final ClientParameters p,
            final AnswerDecoder answerDecoder,
            final OperationReader operationReader
    ) {
        this.p = p;
        this.answerDecoder = answerDecoder;
        this.operationReader = operationReader;
    }

    public void startTalking() {
        final DataInputStream dataInputStream;
        final DataOutputStream dataOutputStream;
        try {
            System.out.println("Trying to establish connection in address:'" + p.getAddress() + "', port:'" + p.getPort() + "'");
            final var socket = new Socket();
            socket.connect(new InetSocketAddress(p.getAddress(), p.getPort()), (int) TIMEOUT_FOR_ESTABLISH.toMillis());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connection established successfully!");
        } catch (SocketTimeoutException e) {
            System.out.println("[TIMEOUT] Connection not established after " + TIMEOUT_FOR_ESTABLISH.getSeconds() + "s.");
            return;
        } catch (IOException e) {
            System.out.println("Not possible to establish connection: " + e.getCause());
            return;
        }

        printInstructions();

        while (true) {
            System.out.println(SEPARATOR);
            final var scanner = new Scanner(System.in);
            System.out.println("Insert next operation.");
            final var s = scanner.nextLine();
            if ("QUIT".equals(s)) {
                break;
            }
            final var operation = operationReader.parse(s);
            if (operation.isEmpty()) {
                System.out.println("Operation inserted not valid, please try again.");
                continue;
            }
            System.out.println("Inserted: " + operation.get().toReadableFormat());

            try {
                dataOutputStream.write(operation.get().encode());
                dataOutputStream.flush();
                System.out.println("Operation sent to the server.");
            } catch (IOException e) {
                System.out.println("Problem writing to the server, try again.");
                continue;
            }
            readAnswerFromServer(dataInputStream);
        }
        System.out.println("Connection ended.");
    }

    private void readAnswerFromServer(final DataInputStream dataInputStream) {
        try {
            final var firstType = dataInputStream.readByte();
            final var size = dataInputStream.readByte();
            final var payload = dataInputStream.readNBytes(size);
            final var buffer = ByteBuffer.allocate(2 + size)
                    .put(firstType)
                    .put(size)
                    .put(payload)
                    .array();
            System.out.println("Answer from server: " + answerDecoder.decodeVariable(buffer));
        } catch (IOException e) {
            System.out.println("Problem reading.");
        }
    }

    private void printInstructions() {
        System.out.println(SEPARATOR);
        System.out.println("Please insert the operation in infix notation: A o B ('o' between space)");
        System.out.println("Numbers (A, B): must be in range [" + OperationReader.MIN_VALUE + ", " + OperationReader.MAX_VALUE + "]");
        System.out.println("Operations (o): Any of:" + stream(Symbol.values()).map(Symbol::toSymbol).collect(Collectors.toList()) + ")");
        System.out.println("Example:");
        System.out.println("1 + 2");
        System.out.println("Type QUIT for exit.");
        System.out.println(SEPARATOR);
    }
}
