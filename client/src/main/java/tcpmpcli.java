import client.Client;
import client.ClientParameters;
import client.answer.AnswerDecoder;
import client.operation.OperationReader;

public class tcpmpcli {
    public static void main(String[] args) {
        ClientParameters
                .parse(args)
                .ifPresentOrElse(
                        tcpmpcli::talk,
                        tcpmpcli::printInstructions
                );
    }

    private static void talk(final ClientParameters params) {
        final var client = new Client(params, new AnswerDecoder(), new OperationReader());
        if (params.isUDP()) {
            System.out.println("Using TCP mode.");
            client.startTalkingUDP();
        } else {
            System.out.println("Using TCP mode.");
            client.startTalking();
        }
    }

    private static void printInstructions() {
        System.out.println();
        System.out.println("Correct format is:");
        System.out.println("tcpmpcli <server_ip> <server_port>");
        System.out.println("for UDP: tcpmpcli <server_ip> <server_port> -u");
        System.out.println();
        System.out.println("\t<server_ip> must be the TCP server IP.");
        System.out.println("\t<server_port> must be the TCP server port.");
        System.out.println("\tadd at the end -u for UDP client (optional)");
        System.out.println();
        System.out.println("Example:");
        System.out.println("tcpmpcli 127.0.0.1 8081");
        System.out.println("java tcpmpcli 127.0.0.1 8081");
        System.out.println("Example for UDP:");
        System.out.println("tcpmpcli 127.0.0.1 8081 -u");
        System.out.println("java tcpmpcli 127.0.0.1 8081 -u");
    }
}
