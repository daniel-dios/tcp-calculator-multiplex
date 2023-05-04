import client.answer.AnswerDecoder;
import org.junit.jupiter.api.Test;
import server.encoder.AnswerEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class GoldenTest {

    private final AnswerEncoder encoder = new AnswerEncoder();
    private final AnswerDecoder decoder = new AnswerDecoder();

    @Test
    void shouldEncodeDecodeSuccess() {
        final var bytes = encoder.encode(10);
        final var actual = decoder.decodeVariable(bytes);

        assertThat(actual)
                .isEqualTo("Message: [Accumulator: [10]].");
    }

    @Test
    void shouldEncodeDecodeFailure() {
        final var bytes = encoder.encodeWithError(10, "Can't divide by 0.");
        final var actual = decoder.decodeVariable(bytes);

        assertThat(actual)
                .isEqualTo("Message: [Text: [Can't divide by 0.], Accumulator: [10]].");
    }
}
