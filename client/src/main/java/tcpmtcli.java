import client.Client;
import client.ClientParameters;
import client.answer.AnswerDecoder;
import client.operation.OperationReader;

public class tcpmtcli {
    public static void main(String[] args) {
        ClientParameters
                .parse(args)
                .ifPresentOrElse(
                        tcpmtcli::talk,
                        tcpmtcli::printInstructions
                );
    }

    private static void talk(final ClientParameters params) {
        final var client = new Client(params, new AnswerDecoder(), new OperationReader());
        client.startTalking();
    }

    private static void printInstructions() {
        System.out.println();
        System.out.println("Correct format is:");
        System.out.println("tcpmtcli <server_ip> <server_port>");
        System.out.println();
        System.out.println("\t<server_ip> must be the TCP server IP.");
        System.out.println("\t<server_port> must be the TCP server port.");
        System.out.println();
        System.out.println("Example:");
        System.out.println("tcpmtcli 0.0.0.0 8081");
        System.out.println("java tcpmtcli 0.0.0.0 8081");
    }
}
