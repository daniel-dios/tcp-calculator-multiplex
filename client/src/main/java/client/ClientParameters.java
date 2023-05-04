package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class ClientParameters {

    private final InetAddress address;
    private final int port;
    private final boolean isUDP;
    private final String operationUDP;

    ClientParameters(
            final InetAddress address,
            final int port,
            final boolean isUDP,
            final String operationUDP
    ) {
        if (!isUDP && operationUDP != null) {
            throw new IllegalArgumentException("tcp with operation?");
        }
        if (isUDP && operationUDP == null) {
            throw new IllegalArgumentException("udp with no operation?");
        }
        this.address = address;
        this.port = port;
        this.isUDP = isUDP;
        this.operationUDP = operationUDP;
    }

    public static Optional<ClientParameters> parse(final String[] args) {
        if (args.length < 2) {
            System.out.println("Missing arguments, read instructions!");
            return Optional.empty();
        }
        if (args.length == 2) {
            return tcp(args);
        }

        if (args.length > 3 && !"-u".equals(args[2])) {
            System.out.println("Missing -u");
            return Optional.empty();
        }

        try {
            final var address = InetAddress.getByName(args[0]);
            final var port = Integer.parseInt(args[1]);
            if (args.length == 4) {
                return Optional.of(new ClientParameters(address, port, true, args[3]));
            }
            if (args.length == 5) {
                return Optional.of(new ClientParameters(address, port, true, args[3] + " " + args[4]));
            }
            if (args.length == 6) {
                return Optional.of(new ClientParameters(address, port, true, args[3] + " " + args[4] + " " + args[5]));
            }
        } catch (UnknownHostException e) {
            System.out.println("First param is not an IP.");
            return Optional.empty();
        } catch (NumberFormatException e) {
            System.out.println("Second param is not a number.");
            return Optional.empty();
        }

        System.out.println("Wrong input, read instructions!");
        return Optional.empty();
    }

    private static Optional<ClientParameters> tcp(final String[] args) {
        try {
            final var address = InetAddress.getByName(args[0]);
            final var port = Integer.parseInt(args[1]);
            return Optional.of(
                    new ClientParameters(
                            address,
                            port,
                            false,
                            null
                    )
            );
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

    public boolean isUDP() {
        return this.isUDP;
    }

    public String getOperationUDP() {
        return this.operationUDP;
    }
}
