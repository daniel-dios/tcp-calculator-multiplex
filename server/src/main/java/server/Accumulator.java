package server;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

public class Accumulator {

    private long value;

    public Accumulator(final long value) {
        this.value = value;
    }

    public void accumulate(long input) throws AccumulatorMax, AccumulatorMin {
        if (input >= 0 && MAX_VALUE - input < value) {
            throw new AccumulatorMax();
        } else if (input < 0 && MIN_VALUE - input > value) {
            throw new AccumulatorMin();
        } else {
            value += input;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public long getValue() {
        return this.value;
    }

    static class AccumulatorMax extends Exception {
    }

    static class AccumulatorMin extends Exception {
    }
}