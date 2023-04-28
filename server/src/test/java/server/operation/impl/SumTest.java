package server.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.operation.Result;

import static org.assertj.core.api.Assertions.assertThat;

class SumTest {

    public static Stream<Arguments> getValues() {
        return Stream.of(
                Arguments.of(1, 100, Result.success(101)),
                Arguments.of(-1, 100, Result.success(99)),
                Arguments.of(-100, 100, Result.success(0)),
                Arguments.of(10, 2, Result.success(12)),
                Arguments.of(127, 10, Result.success(137)),
                Arguments.of(-1, 0, Result.success(-1)),
                Arguments.of(-1, -10, Result.success(-11)),
                Arguments.of(0, 0, Result.success(0))
        );
    }

    @ParameterizedTest
    @MethodSource("getValues")
    void shouldDiv(int a, int b, Result expected) {
        final var sum = new Sum((byte) a, (byte) b);
        assertThat(sum.solve())
                .isEqualTo(expected);
    }
}