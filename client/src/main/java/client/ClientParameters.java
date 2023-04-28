package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class ClientParameters {

    private final InetAddress address;
    private final int port;

    public ClientParameters(
            final InetAddress address,
            final int port
    ) {
        this.address = address;
        this.port = port;
    }

    public static Optional<ClientParameters> parse(final String[] args) {
        if (args.length != 2) {
            System.out.println("Args are not 2");
            return Optional.empty();
        }

        try {
            final var address = InetAddress.getByName(args[0]);
            final var port = Integer.parseInt(args[1]);
            return Optional.of(
                    new ClientParameters(
                            address,
                            port
                    ));
        } catch (UnknownHostException e) {
            System.out.println("First param is not an IP.");
            return Optional.empty();
        } catch (NumberFormatException e) {
            System.out.println("Second param is not a number.");
            return Optional.empty();
        }
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
