package server.operation.impl;

import server.operation.Operation;
import server.operation.Result;

public class Div implements Operation {
    private final byte a;
    private final byte b;

    public Div(final byte a, final byte b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Result solve() {
        if (b == 0) {
            return Result.failure("Can't divide by 0.");
        }
        return Result.success(a / b);
    }

    @Override
    public String toReadableFormat() {
        return a + ":" + b;
    }
}
