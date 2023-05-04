package server;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

public class Accumulator {

    private long value;

    public Accumulator(final long value) {
        this.value = value;
    }

    void accumulate(long input) throws AccumulatorMax, AccumulatorMin {
        if (input >= 0 && MAX_VALUE - input < value) {
            throw new AccumulatorMax();
        } else if (input < 0 && MIN_VALUE - input > value) {
            throw new AccumulatorMin();
        } else {
            value += input;
        }
    }

    public Accumulator accumulateInmutable(long input) throws AccumulatorMax, AccumulatorMin {
        accumulate(input);
        return new Accumulator(this.value);
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