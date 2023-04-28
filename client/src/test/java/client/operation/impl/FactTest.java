package client.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class FactTest {

    public static Stream<Arguments> getExpected() {
        return Stream.of(
                Arguments.of(new Fact(0), new byte[]{6, 1, 0}),
                Arguments.of(new Fact(127), new byte[]{6, 1, 127}),
                Arguments.of(new Fact(1), new byte[]{6, 1, 1})
        );
    }

    @ParameterizedTest
    @MethodSource("getExpected")
    void shouldEncodeProperly(
            final Fact actual,
            final byte[] expected
    ) {
        assertThat(actual.encode())
                .isEqualTo(expected);
    }
}