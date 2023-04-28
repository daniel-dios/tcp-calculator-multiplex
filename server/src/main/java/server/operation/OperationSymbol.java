package server.operation;

import java.util.Optional;

public enum OperationSymbol {
    SUBS,
    MULT,
    DIV,
    DIV_REST,
    FACT,
    SUM;

    public static Optional<OperationSymbol> parse(final byte b) {
        switch (b) {
            case 1:
                return Optional.of(SUM);
            case 2:
                return Optional.of(SUBS);
            case 3:
                return Optional.of(MULT);
            case 4:
                return Optional.of(DIV);
            case 5:
                return Optional.of(DIV_REST);
            case 6:
                return Optional.of(FACT);
        }
        return Optional.empty();
    }
}
