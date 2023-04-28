package server.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.operation.Result;

import static org.assertj.core.api.Assertions.assertThat;

class MulTest {

    public static Stream<Arguments> getValues() {
        return Stream.of(
                Arguments.of(1, 100, Result.success(100)),
                Arguments.of(-1, 100, Result.success(-100)),
                Arguments.of(-100, 100, Result.success(-10000)),
                Arguments.of(10, 2, Result.success(20)),
                Arguments.of(127, 10, Result.success(1270)),
                Arguments.of(-1, 0, Result.success(0)),
                Arguments.of(-1, -10, Result.success(10)),
                Arguments.of(0, 0, Result.success(0))
        );
    }

    @ParameterizedTest
    @MethodSource("getValues")
    void shouldDiv(int a, int b, Result expected) {
        final var mult = new Mult((byte) a, (byte) b);
        assertThat(mult.solve())
                .isEqualTo(expected);
    }
}