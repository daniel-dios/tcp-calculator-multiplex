import server.operation.OperationDecoder;
import server.Server;
import server.ServerParameters;
import server.encoder.AnswerEncoder;

public class tcpmtser {
    public static void main(String[] args) {
        ServerParameters
                .parse(args)
                .ifPresentOrElse(
                        tcpmtser::listen,
                        tcpmtser::printInstructions
                );
    }

    private static void listen(final ServerParameters params) {
        final var server = new Server(params, new AnswerEncoder(), new OperationDecoder());
        server.startListeningBlocking();
    }

    private static void printInstructions() {
        System.out.println();
        System.out.println("Correct format is:");
        System.out.println("tcpmtser <server_port>");
        System.out.println();
        System.out.println("\t<server_port> must be an unused TCP port.");
        System.out.println();
        System.out.println("Example:");
        System.out.println("tcpmtser 8081");
        System.out.println("java tcpmtser 8081");
    }
}
