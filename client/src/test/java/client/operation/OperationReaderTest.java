package client.operation;

import client.operation.impl.Div;
import client.operation.impl.DivRest;
import client.operation.impl.Fact;
import client.operation.impl.Mult;
import client.operation.impl.Subs;
import client.operation.impl.Sum;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class OperationReaderTest {

    private final OperationReader operationReader = new OperationReader();

    public static Stream<Arguments> getExpected() {
        return Stream.of(
                Arguments.of("1 x 2", new Mult(1, 2)),
                Arguments.of("-1 x 2", new Mult(-1, 2)),
                Arguments.of("1 x -2", new Mult(1, -2)),
                Arguments.of("-1 x -2", new Mult(-1, -2)),

                Arguments.of("1 + 2", new Sum(1, 2)),
                Arguments.of("-1 + 2", new Sum(-1, 2)),
                Arguments.of("1 + -2", new Sum(1, -2)),
                Arguments.of("-1 + -2", new Sum(-1, -2)),

                Arguments.of("1 / 2", new Div(1, 2)),
                Arguments.of("-1 / 2", new Div(-1, 2)),
                Arguments.of("1 / -2", new Div(1, -2)),
                Arguments.of("-1 / -2", new Div(-1, -2)),

                Arguments.of("1 % 2", new DivRest(1, 2)),
                Arguments.of("-1 % 2", new DivRest(-1, 2)),
                Arguments.of("1 % -2", new DivRest(1, -2)),
                Arguments.of("-1 % -2", new DivRest(-1, -2)),

                Arguments.of("1 - 2", new Subs(1, 2)),
                Arguments.of("-1 - 2", new Subs(-1, 2)),
                Arguments.of("1 - -2", new Subs(1, -2)),
                Arguments.of("-1 - -2", new Subs(-1, -2)),

                Arguments.of("1 !", new Fact(1)),
                Arguments.of("-1 !", new Fact(-1)),
                Arguments.of("1!", new Fact(1)),
                Arguments.of("-1!", new Fact(-1)),

                Arguments.of("127 x 127", new Mult(127, 127)),
                Arguments.of("-128 x 127", new Mult(-128, 127)),
                Arguments.of("127 x -128", new Mult(127, -128)),
                Arguments.of("-128 x -128", new Mult(-128, -128)),
                Arguments.of("127 + 127", new Sum(127, 127)),
                Arguments.of("-128 + 127", new Sum(-128, 127)),
                Arguments.of("127 + -128", new Sum(127, -128)),
                Arguments.of("-128 + -128", new Sum(-128, -128)),
                Arguments.of("127 / 127", new Div(127, 127)),
                Arguments.of("-128 / 127", new Div(-128, 127)),
                Arguments.of("127 / -128", new Div(127, -128)),
                Arguments.of("-128 / -128", new Div(-128, -128)),
                Arguments.of("127 % 127", new DivRest(127, 127)),
                Arguments.of("-128 % 127", new DivRest(-128, 127)),
                Arguments.of("127 % -128", new DivRest(127, -128)),
                Arguments.of("-128 % -128", new DivRest(-128, -128)),
                Arguments.of("127 - 127", new Subs(127, 127)),
                Arguments.of("-128 - 127", new Subs(-128, 127)),
                Arguments.of("127 - -128", new Subs(127, -128)),
                Arguments.of("-128 - -128", new Subs(-128, -128)),
                Arguments.of("127 !", new Fact(127)),


                Arguments.of("        -128 !        ", new Fact(-128)),
                Arguments.of("        127!      ", new Fact(127)),
                Arguments.of("        -128!         ", new Fact(-128)),

                Arguments.of("        1 x 2         ", new Mult(1, 2)),
                Arguments.of("        -1 x 2        ", new Mult(-1, 2)),
                Arguments.of("        1 x -2        ", new Mult(1, -2)),
                Arguments.of("        -1 x -2       ", new Mult(-1, -2)),

                Arguments.of("        1 + 2         ", new Sum(1, 2)),
                Arguments.of("        -1 + 2        ", new Sum(-1, 2)),
                Arguments.of("        1 + -2        ", new Sum(1, -2)),
                Arguments.of("        -1 + -2       ", new Sum(-1, -2)),

                Arguments.of("        1 / 2         ", new Div(1, 2)),
                Arguments.of("        -1 / 2        ", new Div(-1, 2)),
                Arguments.of("        1 / -2        ", new Div(1, -2)),
                Arguments.of("        -1 / -2       ", new Div(-1, -2)),

                Arguments.of("        1 % 2         ", new DivRest(1, 2)),
                Arguments.of("        -1 % 2        ", new DivRest(-1, 2)),
                Arguments.of("        1 % -2        ", new DivRest(1, -2)),
                Arguments.of("        -1 % -2       ", new DivRest(-1, -2)),

                Arguments.of("        1 - 2         ", new Subs(1, 2)),
                Arguments.of("        -1 - 2        ", new Subs(-1, 2)),
                Arguments.of("        1 - -2        ", new Subs(1, -2)),
                Arguments.of("        -1 - -2       ", new Subs(-1, -2)),

                Arguments.of("        1 !       ", new Fact(1)),
                Arguments.of("        -1 !      ", new Fact(-1)),
                Arguments.of("        1!        ", new Fact(1)),
                Arguments.of("        -1!       ", new Fact(-1)),

                Arguments.of("        127 x 127         ", new Mult(127, 127)),
                Arguments.of("        -128 x 127        ", new Mult(-128, 127)),
                Arguments.of("        127 x -128        ", new Mult(127, -128)),
                Arguments.of("        -128 x -128       ", new Mult(-128, -128)),
                Arguments.of("        127 + 127         ", new Sum(127, 127)),
                Arguments.of("        -128 + 127        ", new Sum(-128, 127)),
                Arguments.of("        127 + -128        ", new Sum(127, -128)),
                Arguments.of("        -128 + -128       ", new Sum(-128, -128)),
                Arguments.of("        127 / 127         ", new Div(127, 127)),
                Arguments.of("        -128 / 127        ", new Div(-128, 127)),
                Arguments.of("        127 / -128        ", new Div(127, -128)),
                Arguments.of("        -128 / -128       ", new Div(-128, -128)),
                Arguments.of("        127 % 127         ", new DivRest(127, 127)),
                Arguments.of("        -128 % 127        ", new DivRest(-128, 127)),
                Arguments.of("        127 % -128        ", new DivRest(127, -128)),
                Arguments.of("        -128 % -128       ", new DivRest(-128, -128)),
                Arguments.of("        127 - 127         ", new Subs(127, 127)),
                Arguments.of("        -128 - 127        ", new Subs(-128, 127)),
                Arguments.of("        127 - -128        ", new Subs(127, -128)),
                Arguments.of("        -128 - -128       ", new Subs(-128, -128)),
                Arguments.of("        127 !         ", new Fact(127)),
                Arguments.of("        -128 !        ", new Fact(-128)),
                Arguments.of("        127!      ", new Fact(127)),
                Arguments.of("        -128!         ", new Fact(-128))
        );
    }

    public static Stream<Arguments> getWrongOverRange() {
        return Stream.of(
                Arguments.of("1 x2"),
                Arguments.of("-1 x2"),
                Arguments.of("1 x-2"),
                Arguments.of("-1 x-2"),

                Arguments.of("1 +2"),
                Arguments.of("-1 +2"),
                Arguments.of("1 +-2"),
                Arguments.of("-1 +-2"),

                Arguments.of("1 /2"),
                Arguments.of("-1 /2"),
                Arguments.of("1 /-2"),
                Arguments.of("-1 /-2"),

                Arguments.of("1 %2"),
                Arguments.of("-1 %2"),
                Arguments.of("1 %-2"),
                Arguments.of("-1 %-2"),

                Arguments.of("1 -2"),
                Arguments.of("-1 -2"),
                Arguments.of("1 --2"),
                Arguments.of("-1 --2"),

                Arguments.of("1x 2"),
                Arguments.of("-1x 2"),
                Arguments.of("1x -2"),
                Arguments.of("-1x -2"),

                Arguments.of("1+ 2"),
                Arguments.of("-1+ 2"),
                Arguments.of("1+ -2"),
                Arguments.of("-1+ -2"),

                Arguments.of("1/ 2"),
                Arguments.of("-1/ 2"),
                Arguments.of("1/ -2"),
                Arguments.of("-1/ -2"),

                Arguments.of("1% 2"),
                Arguments.of("-1% 2"),
                Arguments.of("1% -2"),
                Arguments.of("-1% -2"),

                Arguments.of("1- 2"),
                Arguments.of("-1- 2"),
                Arguments.of("1- -2"),
                Arguments.of("-1- -2"),

                Arguments.of("1x2"),
                Arguments.of("-1x2"),
                Arguments.of("1x-2"),
                Arguments.of("-1x-2"),

                Arguments.of("1+2"),
                Arguments.of("-1+2"),
                Arguments.of("1+-2"),
                Arguments.of("-1+-2"),

                Arguments.of("1/2"),
                Arguments.of("-1/2"),
                Arguments.of("1/-2"),
                Arguments.of("-1/-2"),

                Arguments.of("1%2"),
                Arguments.of("-1%2"),
                Arguments.of("1%-2"),
                Arguments.of("-1%-2"),

                Arguments.of("1-2"),
                Arguments.of("-1-2"),
                Arguments.of("1--2"),
                Arguments.of("-1--2"),

                Arguments.of("1 x 128"),
                Arguments.of("128 x 2"),

                Arguments.of("2 - 128"),
                Arguments.of("128 - 127"),

                Arguments.of("2 + 128"),
                Arguments.of("128 + 127"),

                Arguments.of("128 / 1"),
                Arguments.of("12 / 128"),

                Arguments.of("128 % 1"),
                Arguments.of("12 % 128"),

                Arguments.of("128 !"),

                Arguments.of("1 x -129"),
                Arguments.of("-129 x 2"),

                Arguments.of("2 - -129"),
                Arguments.of("-129 - 127"),

                Arguments.of("2 + -129"),
                Arguments.of("-129 + 127"),

                Arguments.of("-129 / 1"),
                Arguments.of("12 / -129"),

                Arguments.of("-129 % 1"),
                Arguments.of("12 % -129"),

                Arguments.of("-129 !")
        );
    }

    public static Stream<Arguments> getWrong() {
        return Stream.of(
                Arguments.of("1 x x2"),
                Arguments.of(" + 2"),
                Arguments.of("2 -- 1"),
                Arguments.of("3 / 1 $"),
                Arguments.of("%6 % 3"),
                Arguments.of("3 !! 32"),
                Arguments.of("1x+2"),
                Arguments.of("0+2 3"),
                Arguments.of("2-1+"),
                Arguments.of("3/1x"),
                Arguments.of("6%%%%%3"),
                Arguments.of("-"),
                Arguments.of("3 1 x"),
                Arguments.of("x 6 2"),
                Arguments.of("3 2")
        );
    }

    @ParameterizedTest
    @MethodSource("getExpected")
    void shouldReturnExpected(final String input, final Operation expected) {
        assertThat(operationReader.parse(input))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getWrong")
    void shouldReturnEmpty(final String input) {
        assertThat(operationReader.parse(input))
                .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getWrongOverRange")
    void shouldReturnEmptyWhenOutOfRange(final String input) {
        assertThat(operationReader.parse(input))
                .isEmpty();
    }
}