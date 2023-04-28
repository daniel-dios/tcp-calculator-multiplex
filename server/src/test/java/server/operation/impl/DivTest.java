package server.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.operation.Result;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

class DivTest {

    public static Stream<Arguments> getValues() {
        return Stream.of(
                Arguments.of(1, 100, Result.success(0)),
                Arguments.of(-1, 100, Result.success(0)),
                Arguments.of(-100, 100, Result.success(-1)),
                Arguments.of(10, 2, Result.success(5)),
                Arguments.of(127, 10, Result.success(12)),
                Arguments.of(-1, 0, Result.failure("Can't divide by 0.")),
                Arguments.of(0, 0, Result.failure("Can't divide by 0."))
        );
    }

    @ParameterizedTest
    @MethodSource("getValues")
    void shouldDiv(int a, int b, Result expected) {
        final var div = new Div((byte) a, (byte) b);
        assertThat(div.solve())
                .isEqualTo(expected);
    }
}