package server.operation.impl;

import server.operation.Operation;
import server.operation.Result;

public class Fact implements Operation {
    private final byte a;

    public Fact(final byte a) {
        this.a = a;
    }

    @Override
    public Result solve() {
        if (a < 0) {
            return Result.failure("Can't fact number < 0.");
        }
        if (a > 20) {
            return Result.failure("Factorial overflow!");
        }
        long start = 1;
        for (int i = 1; i <= a; i++) {
            start *= i;
        }
        return Result.success(start);
    }

    @Override
    public String toReadableFormat() {
        return a + "!";
    }
}
