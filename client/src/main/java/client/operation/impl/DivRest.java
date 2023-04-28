package client.operation.impl;

import client.operation.Operation;
import client.operation.Symbol;

public class DivRest implements Operation {
    private final int first;
    private final int second;

    public DivRest(
            final int first,
            final int second
    ) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toReadableFormat() {
        return first + " " + Symbol.DIV_REST + " " + second;
    }

    @Override
    public byte[] encode() {
        final byte[] encoded = new byte[4];
        encoded[0] = 5;
        encoded[1] = 2;
        encoded[2] = (byte) first;
        encoded[3] = (byte) second;
        return encoded;
    }
}
