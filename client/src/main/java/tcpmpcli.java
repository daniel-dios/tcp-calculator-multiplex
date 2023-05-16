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
            System.out.println("Using UDP mode.");
            client.talkUDP(params.getOperationUDP());
        } else {
            System.out.println("Using TCP mode.");
            client.startTalking();
        }
    }

    private static void printInstructions() {
        System.out.println();
        System.out.println("Correct format is:");
        System.out.println("tcpmpcli <server_ip> <server_port>");
        System.out.println("for UDP: tcpmpcli <server_ip> <server_port> -u <n1> <o> <n2>");
        System.out.println("for UDP fact: tcpmpcli <server_ip> <server_port> -u <n1> !");
        System.out.println();
        System.out.println("\t<server_ip> must be the TCP server IP.");
        System.out.println("\t<server_port> must be the TCP server port.");
        System.out.println("\t<n1>, <n2> numbers of range [-128, 127] and o any of (+, -, x, /, %)");
        System.out.println("\t<o> any of (+, -, x, /, %)");
        System.out.println();

        System.out.println("Example:");
        System.out.println("tcpmpcli 127.0.0.1 8081");
        System.out.println("java tcpmpcli 127.0.0.1 8081");

        System.out.println();
        System.out.println("Example for UDP:");
        System.out.println("tcpmpcli 127.0.0.1 8081 -u 1 + 2");
        System.out.println("java tcpmpcli 127.0.0.1 8081 -u 1 + 2");
    }
}
