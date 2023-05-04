package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientParametersTest {

    private final String port = "8080";
    private final String ip = "127.0.0.1";

    @Test
    void shouldAllowUDPOption() {
        final var actual = ClientParameters.parse(new String[]{ip, port, "-u", "2", "+", "3"});

        assertThat(actual).isPresent();
        assertThat(actual.get().getAddress().getHostAddress()).isEqualTo(ip);
        assertThat(actual.get().getPort()).isEqualTo(8080);
        assertThat(actual.get().isUDP()).isTrue();
        assertThat(actual.get().getOperationUDP()).isEqualTo("2 + 3");
    }

    @Test
    void shouldAllowUDPOptionWithFact() {
        final var actual = ClientParameters.parse(new String[]{ip, port, "-u", "2", "!"});

        assertThat(actual).isPresent();
        assertThat(actual.get().getAddress().getHostAddress()).isEqualTo(ip);
        assertThat(actual.get().getPort()).isEqualTo(8080);
        assertThat(actual.get().isUDP()).isTrue();
        assertThat(actual.get().getOperationUDP()).isEqualTo("2 !");
    }

    @Test
    void shouldAllowUDPOptionWithCompacted() {
        final var actual = ClientParameters.parse(new String[]{ip, port, "-u", "2!"});

        assertThat(actual).isPresent();
        assertThat(actual.get().getAddress().getHostAddress()).isEqualTo(ip);
        assertThat(actual.get().getPort()).isEqualTo(8080);
        assertThat(actual.get().isUDP()).isTrue();
        assertThat(actual.get().getOperationUDP()).isEqualTo("2!");
    }

    @Test
    void shouldFailWhenUDPHasNoOperation() {
        assertThat(ClientParameters.parse(new String[]{ip, port, "-u"}))
                .isEmpty();
    }

    @Test
    void shouldFailWhenUDPHasMoreThanExpected() {
        assertThat(ClientParameters.parse(new String[]{ip, port, "-u", "3", "23", "e", "123"}))
                .isEmpty();
    }

    @Test
    void shouldFailWhenUDPIsWrongCommand() {
        assertThat(ClientParameters.parse(new String[]{ip, port, "U"}))
                .isEmpty();
    }

    @Test
    void shouldParseValidArgs() {
        final var actual = ClientParameters.parse(new String[]{ip, port});

        assertThat(actual).isPresent();
        assertThat(actual.get().getAddress().getHostAddress()).isEqualTo(ip);
        assertThat(actual.get().getPort()).isEqualTo(8080);
        assertThat(actual.get().isUDP()).isFalse();
    }

    @Test
    void shouldParseInvalidArgsLength() {
        assertThat(ClientParameters.parse(new String[]{ip}))
                .isEmpty();
    }

    @Test
    void shouldParseInvalidIPAddress() {
        assertThat(ClientParameters.parse(new String[]{"invalid-ip", port}))
                .isEmpty();
    }

    @Test
    void shouldParseInvalidPort() {
        assertThat(ClientParameters.parse(new String[]{ip, "invalid-port"}))
                .isEmpty();
    }

    @Test
    void shouldFailWithUDPNoOperation() {
        assertThatThrownBy(() -> new ClientParameters(InetAddress.getByName(ip), 9090, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFailWithTCPAndOperation() {
        assertThatThrownBy(() -> new ClientParameters(InetAddress.getByName(ip), 9090, false, "something"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldGetWithNoUDP() throws UnknownHostException {
        final var address = InetAddress.getByName(ip);
        final var clientParameters = new ClientParameters(address, 8080, false, null);

        assertThat(clientParameters.getAddress()).isEqualTo(address);
        assertThat(clientParameters.getPort()).isEqualTo(8080);
        assertThat(clientParameters.isUDP()).isFalse();
    }

    @Test
    void shouldGetAddress() throws UnknownHostException {
        final var address = InetAddress.getByName(ip);
        final var clientParameters = new ClientParameters(address, 8080, true, "2 + 1");

        assertThat(clientParameters.getAddress()).isEqualTo(address);
        assertThat(clientParameters.getPort()).isEqualTo(8080);
        assertThat(clientParameters.isUDP()).isTrue();
    }
}
