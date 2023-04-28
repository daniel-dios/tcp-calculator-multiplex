package client.operation.impl;

import client.operation.Operation;
import client.operation.Symbol;

public class Fact implements Operation {
    private final int first;

    public Fact(
            final int first
    ) {
        this.first = first;
    }

    @Override
    public String toReadableFormat() {
        return first + " " + Symbol.FACT;
    }

    @Override
    public byte[] encode() {
        final byte[] encoded = new byte[3];
        encoded[0] = 6;
        encoded[1] = 1;
        encoded[2] = (byte) first;
        return encoded;
    }

    @Override
    public String toString() {
        return toReadableFormat();
    }
}
