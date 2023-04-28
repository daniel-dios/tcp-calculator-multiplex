package server.operation;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.operation.impl.Div;
import server.operation.impl.DivRest;
import server.operation.impl.Fact;
import server.operation.impl.Mult;
import server.operation.impl.Subs;
import server.operation.impl.Sum;

import static org.assertj.core.api.Assertions.assertThat;

class OperationDecoderTest {

    public static Stream<Arguments> getExamples() {
        return Stream.of(
                Arguments.of(new byte[]{1, 2, 120, 54}, new Sum((byte) 120, (byte) 54)),
                Arguments.of(new byte[]{2, 2, 25, 10}, new Subs((byte) 25, (byte) 10)),
                Arguments.of(new byte[]{3, 2, -5, 8}, new Mult((byte) -5, (byte) 8)),
                Arguments.of(new byte[]{4, 2, 40, 5}, new Div((byte) 40, (byte) 5)),
                Arguments.of(new byte[]{5, 2, 2, 7}, new DivRest((byte) 2, (byte) 7)),
                Arguments.of(new byte[]{6, 1, 5, 0}, new Fact((byte) 5))
        );
    }

    @ParameterizedTest
    @MethodSource("getExamples")
    void shouldDecodeExamples(byte[] input, Operation expected) {
        final var maybeOperation = new OperationDecoder().decode(input);
        assertThat(maybeOperation)
                .isPresent();
        assertThat(maybeOperation.get())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}