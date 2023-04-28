package server.operation;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static server.operation.OperationSymbol.DIV;
import static server.operation.OperationSymbol.DIV_REST;
import static server.operation.OperationSymbol.FACT;
import static server.operation.OperationSymbol.MULT;
import static server.operation.OperationSymbol.SUBS;
import static server.operation.OperationSymbol.SUM;
import static server.operation.OperationSymbol.parse;

class OperationSymbolTest {

    public static Stream<Arguments> getValues() {
        return Stream.of(
                Arguments.of((byte) 1, SUM),
                Arguments.of((byte) 2, SUBS),
                Arguments.of((byte) 3, MULT),
                Arguments.of((byte) 4, DIV),
                Arguments.of((byte) 5, DIV_REST),
                Arguments.of((byte) 6, FACT)
        );
    }

    @ParameterizedTest
    @MethodSource("getValues")
    void shouldReturnExpected(byte input, OperationSymbol expected) {
        assertThat(parse(input))
                .isPresent()
                .contains(expected);
    }
}
