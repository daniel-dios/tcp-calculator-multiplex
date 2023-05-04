package server.encoder;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.shadow.com.univocity.parsers.common.ArgumentUtils.toByteArray;

class AnswerEncoderTest {

    private final AnswerEncoder answerEncoder = new AnswerEncoder();

    public static Stream<Arguments> inputExpected() {
        return Stream.of(
                Arguments.of(-525, new byte[]{10, 10, 16, 8, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 253, (byte) 243}),
                Arguments.of(260, new byte[]{10, 10, 16, 8, 0, 0, 0, 0, 0, 0, 1, 4})
        );
    }

    @ParameterizedTest
    @MethodSource("inputExpected")
    void shouldReturnExpected(
            final long input,
            final byte[] expected
    ) {
        assertThat(answerEncoder.encode(input))
                .isEqualTo(expected);
    }

    @Test
    void shouldEncodeFailure() {
        assertThat(answerEncoder.encodeWithError(-525, "Can not divide by 0"))
                .isEqualTo(toByteArray(
                        10, 31, 11, 19, 67, 97, 110, 32, 110, 111, 116, 32, 100, 105, 118, 105, 100,
                        101, 32, 98, 121, 32, 48, 16, 8, -1, -1, -1, -1, -1, -1, -3, -13
                ));
    }
}
