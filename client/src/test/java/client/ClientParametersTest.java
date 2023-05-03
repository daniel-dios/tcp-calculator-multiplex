package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientParametersTest {

    private final String port = "8080";
    private final String ip = "127.0.0.1";

    @Test
    void shouldAllowUDPOption() {
        final var actual = ClientParameters.parse(new String[]{ip, port, "-u"});

        assertThat(actual).isPresent();
        assertThat(actual.get().getAddress().getHostAddress()).isEqualTo(ip);
        assertThat(actual.get().getPort()).isEqualTo(8080);
        assertThat(actual.get().isUDP()).isTrue();
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
    void shouldGetWithNoUDP() throws UnknownHostException {
        final var address = InetAddress.getByName(ip);
        final var clientParameters = new ClientParameters(address, 8080, false);

        assertThat(clientParameters.getAddress()).isEqualTo(address);
        assertThat(clientParameters.getPort()).isEqualTo(8080);
        assertThat(clientParameters.isUDP()).isFalse();
    }

    @Test
    void shouldGetAddress() throws UnknownHostException {
        final var address = InetAddress.getByName(ip);
        final var clientParameters = new ClientParameters(address, 8080, true);

        assertThat(clientParameters.getAddress()).isEqualTo(address);
        assertThat(clientParameters.getPort()).isEqualTo(8080);
        assertThat(clientParameters.isUDP()).isTrue();
    }
}
