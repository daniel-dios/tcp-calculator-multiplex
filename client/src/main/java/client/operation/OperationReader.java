package client.operation;

import client.operation.impl.Div;
import client.operation.impl.DivRest;
import client.operation.impl.Fact;
import client.operation.impl.Mult;
import client.operation.impl.Subs;
import client.operation.impl.Sum;
import java.util.Arrays;
import java.util.Optional;

public class OperationReader {

    public static final int MIN_VALUE = -128;
    public static final int MAX_VALUE = 127;

    public Optional<Operation> parse(final String input) {
        final var split = input.trim().split(" ");
        if (split.length == 1 && split[0].contains(Symbol.FACT.toSymbol())) {
            final var integer = checkNumber(split[0].split(Symbol.FACT.toSymbol())[0]);
            if (integer.isPresent()) {
                return integer.map(Fact::new);
            } else {
                return printInstructionsAndReturnEmpty();
            }
        }
        if (split.length == 2 && Symbol.FACT.toSymbol().equals(split[1])) {
            return checkNumber(split[0]).map(Fact::new);
        }
        if (split.length == 3 && Symbol.FACT.toSymbol().equals(split[1])) {
            System.out.println("\tFactorial must be only one number.");
            return printInstructionsAndReturnEmpty();
        }
        if (split.length == 3) {
            final var maybeSymbol = Arrays
                    .stream(Symbol.values())
                    .filter(symbol -> symbol.toSymbol().equals(split[1]))
                    .findFirst();
            if (maybeSymbol.isEmpty()) {
                System.out.println("\tThere is no operation symbol in infix with spaces.");
                return printInstructionsAndReturnEmpty();
            }
            final var first = checkNumber(split[0]);
            final var second = checkNumber(split[2]);
            if (first.isPresent() && second.isPresent()) {
                switch (maybeSymbol.get()) {
                    case SUM:
                        return Optional.of(new Sum(first.get(), second.get()));
                    case SUBS:
                        return Optional.of(new Subs(first.get(), second.get()));
                    case DIV:
                        return Optional.of(new Div(first.get(), second.get()));
                    case DIV_REST:
                        return Optional.of(new DivRest(first.get(), second.get()));
                    case MULT:
                        return Optional.of(new Mult(first.get(), second.get()));
                    default:
                        System.out.println("\tThis symbol between numbers not accepted, try with: x, /, %, -");
                        return printInstructionsAndReturnEmpty();
                }
            } else {
                System.out.println("\tThere are not numbers in the valid range");
                return printInstructionsAndReturnEmpty();
            }
        }
        return printInstructionsAndReturnEmpty();
    }

    private Optional<Operation> printInstructionsAndReturnEmpty() {
        System.out.println("\tValid format is infix annotation, symbols are +, -, /, %, x, !");
        System.out.println("\tNumbers range is [-128, 127] inclusive.");
        System.out.println("\tInfix annotation: number symbol number (with spaces between");
        System.out.println("\tnumber and number and no spaces before 1st number and after 2nd)");
        System.out.println("\tExamples: 1 x 2, 1 / 2, 1 % 2, 1 - 2, 1 + 2, 1 ! or 1!");
        return Optional.empty();
    }

    private Optional<Integer> checkNumber(final String input) {
        try {
            final int value = Integer.parseInt(input.trim());
            if (value > MAX_VALUE || value < MIN_VALUE) {
                System.out.println(input + " is out of range.");
                return Optional.empty();
            }
            return Optional.of(value);
        } catch (NumberFormatException ex) {
            System.out.println(input + " is not a valid number.");
            return Optional.empty();
        }
    }
}
