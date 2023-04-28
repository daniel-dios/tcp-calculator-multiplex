package client.operation.impl;

import client.operation.Operation;
import client.operation.Symbol;

public class Sum implements Operation {
    private final int first;
    private final int second;

    public Sum(
            final int first,
            final int second
    ) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toReadableFormat() {
        return first + " " + Symbol.SUM + " " + second;
    }

    @Override
    public byte[] encode() {
        final byte[] encoded = new byte[4];
        encoded[0] = 1;
        encoded[1] = 2;
        encoded[2] = (byte) first;
        encoded[3] = (byte) second;
        return encoded;
    }
}
