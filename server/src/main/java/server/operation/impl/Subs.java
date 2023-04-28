package server.operation.impl;

import server.operation.Operation;
import server.operation.Result;

public class Subs implements Operation {
    private final byte a;
    private final byte b;

    public Subs(final byte a, final byte b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Result solve() {
        return Result.success(a - b);
    }

    @Override
    public String toReadableFormat() {
        return a + "-" + b;
    }
}
