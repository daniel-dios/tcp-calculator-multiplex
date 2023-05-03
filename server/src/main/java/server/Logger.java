package server;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public class Logger {
    private final String header;

    public Logger(final DatagramChannel datagramChannel) {
        final var builder = new StringBuilder().append("[UDP] ");
        try {
            builder.append(datagramChannel.getRemoteAddress());
        } catch (IOException e) {
            // blank
        }
        this.header = builder.toString();
    }

    public Logger(final SocketChannel socketChannel) {
        final var builder = new StringBuilder().append("[TCP] ");
        try {
            builder.append(socketChannel.getRemoteAddress());
        } catch (IOException e) {
            // blank
        }
        this.header = builder.toString();
    }

    public void info(String message) {
        System.out.println(header + message);
    }
}
