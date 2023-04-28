package client.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class SumTest {

    public static Stream<Arguments> getExpected() {
        return Stream.of(
                Arguments.of(new Sum(0, 0), new byte[]{1, 2, 0, 0}),
                Arguments.of(new Sum(0, 127), new byte[]{1, 2, 0, 127}),
                Arguments.of(new Sum(1, 2), new byte[]{1, 2, 1, 2}),
                Arguments.of(new Sum(127, 127), new byte[]{1, 2, 127, 127})
        );
    }

    @ParameterizedTest
    @MethodSource("getExpected")
    void shouldEncodeProperly(
            final Sum actual,
            final byte[] expected
    ) {
        assertThat(actual.encode())
                .isEqualTo(expected);
    }
}