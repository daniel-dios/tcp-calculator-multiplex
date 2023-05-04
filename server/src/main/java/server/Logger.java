package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Logger {
    private final String header;

    public Logger(final InetSocketAddress address) {
        this.header = String.format("[UDP] %s:%d ", address.getAddress(), address.getPort());
    }

    public Logger(final SocketChannel socketChannel) {
        final var builder = new StringBuilder().append("[TCP] ");
        try {
            builder.append(socketChannel.getRemoteAddress());
        } catch (IOException e) {
            // blank
        }
        this.header = builder.append("  ").toString();
    }

    public void info(String message) {
        System.out.println(header + message);
    }
}
