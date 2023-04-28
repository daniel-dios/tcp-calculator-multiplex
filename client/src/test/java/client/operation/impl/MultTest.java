package client.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class MultTest {

    public static Stream<Arguments> getExpected() {
        return Stream.of(
                Arguments.of(new Mult(0, 0), new byte[]{3, 2, 0, 0}),
                Arguments.of(new Mult(0, 127), new byte[]{3, 2, 0, 127}),
                Arguments.of(new Mult(1, 2), new byte[]{3, 2, 1, 2}),
                Arguments.of(new Mult(127, 127), new byte[]{3, 2, 127, 127})
        );
    }

    @ParameterizedTest
    @MethodSource("getExpected")
    void shouldEncodeProperly(
            final Mult actual,
            final byte[] expected
    ) {
        assertThat(actual.encode())
                .isEqualTo(expected);
    }
}