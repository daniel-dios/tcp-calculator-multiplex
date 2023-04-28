package server.operation;

import java.util.Optional;
import server.operation.impl.Div;
import server.operation.impl.DivRest;
import server.operation.impl.Fact;
import server.operation.impl.Mult;
import server.operation.impl.Subs;
import server.operation.impl.Sum;

public class OperationDecoder {

    public static final int OPERATION_POSITION = 0;
    public static final int SIZE_POSITION = 1;
    public static final int FIRST_NUMBER_POSITION = 2;
    public static final int SECOND_NUMBER_POSITION = 3;

    public Optional<Operation> decode(final byte[] buffer) {
        final var operation = OperationSymbol.parse(buffer[OPERATION_POSITION]);
        final var size = buffer[SIZE_POSITION];

        if (operation.isEmpty()) {
            System.out.println("Operation invalid.");
            return Optional.empty();
        }

        if (!(size == 1 || size == 2)) {
            System.out.println("Size " + size + " is not valid.");
            return Optional.empty();
        }
        if (operation.get() == OperationSymbol.FACT && size != 1) {
            System.out.println("Received Factorial with size 2.");
            return Optional.empty();
        }

        switch (operation.get()) {
            case SUBS:
                return Optional.of(new Subs(buffer[FIRST_NUMBER_POSITION], buffer[SECOND_NUMBER_POSITION]));
            case MULT:
                return Optional.of(new Mult(buffer[FIRST_NUMBER_POSITION], buffer[SECOND_NUMBER_POSITION]));
            case DIV:
                return Optional.of(new Div(buffer[FIRST_NUMBER_POSITION], buffer[SECOND_NUMBER_POSITION]));
            case DIV_REST:
                return Optional.of(new DivRest(buffer[FIRST_NUMBER_POSITION], buffer[SECOND_NUMBER_POSITION]));
            case FACT:
                return Optional.of(new Fact(buffer[FIRST_NUMBER_POSITION]));
            case SUM:
                return Optional.of(new Sum(buffer[FIRST_NUMBER_POSITION], buffer[SECOND_NUMBER_POSITION]));
        }

        return Optional.empty();
    }
}
