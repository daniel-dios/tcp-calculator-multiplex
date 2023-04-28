package server;

import java.util.Optional;

public class ServerParameters {
    private final int port;

    public ServerParameters(final int port) {
        this.port = port;
    }

    public static Optional<ServerParameters> parse(
            final String[] args
    ) {
        if (args.length != 1) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ServerParameters(Integer.parseInt(args[0])));
        } catch (NumberFormatException ex) {
            System.out.println("Arg is not a number!");
            return Optional.empty();
        }
    }

    public int getPort() {
        return this.port;
    }
}
