package server.operation.impl;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.operation.Result;

import static java.lang.Long.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;


class FactTest {

    public static Stream<Arguments> getValues() {
        return Stream.of(
                Arguments.of(0, Result.success(1)),
                Arguments.of(1,  Result.success(1)),
                Arguments.of(5,  Result.success(120)),
                Arguments.of(10, Result.success(3628800)),
                Arguments.of(18, Result.success(6402373705728000L)),
                Arguments.of(19, Result.success(121645100408832000L)),
                Arguments.of(20, Result.success(2432902008176640000L)),
                Arguments.of(21, Result.failure("Factorial overflow!")),
                Arguments.of(127,Result.failure( "Factorial overflow!")),
                Arguments.of(-1,Result.failure( "Can't fact number < 0."))
        );
    }

    @ParameterizedTest
    @MethodSource("getValues")
    void shouldDiv(int a, Result expected) {
        final var fact = new Fact((byte) a);
        assertThat(fact.solve())
                .isEqualTo(expected);
    }
}