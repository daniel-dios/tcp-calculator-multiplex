package client.operation;

public enum Symbol {
    SUM("+"),
    SUBS("-"),
    DIV("/"),
    DIV_REST("%"),
    FACT("!"),
    MULT("x");

    private final String symbol;

    Symbol(final String symbol) {
        this.symbol = symbol;
    }
    public String toSymbol() {
        return symbol;
    }
}
