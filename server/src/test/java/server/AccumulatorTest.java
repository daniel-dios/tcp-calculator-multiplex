package server;

import org.junit.jupiter.api.Test;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccumulatorTest {

    @Test
    void shouldAccumulate() throws Accumulator.AccumulatorMax, Accumulator.AccumulatorMin {
        final var accumulator = new Accumulator(0);

        assertThat(accumulator.getValue())
                .isEqualTo(0);

        accumulator.accumulate(100000);

        assertThat(accumulator.getValue())
                .isEqualTo(100000);

        accumulator.accumulate(-1000);

        assertThat(accumulator.getValue())
                .isEqualTo(99000);
    }

    @Test
    void shouldAccumulateUntilMax() throws Accumulator.AccumulatorMax, Accumulator.AccumulatorMin {
        final var accumulator = new Accumulator(0);

        assertThat(accumulator.getValue())
                .isEqualTo(0);

        accumulator.accumulate(100000);

        assertThat(accumulator.getValue())
                .isEqualTo(100000);

        accumulator.accumulate(-1000);

        assertThat(accumulator.getValue())
                .isEqualTo(99000);

        assertThatThrownBy(() -> accumulator.accumulate(MAX_VALUE))
                .isInstanceOf(Accumulator.AccumulatorMax.class);

        accumulator.accumulate(MAX_VALUE - 99000);
        assertThat(accumulator.getValue())
                .isEqualTo(MAX_VALUE);

        assertThatThrownBy(() -> accumulator.accumulate(1))
                .isInstanceOf(Accumulator.AccumulatorMax.class);

        accumulator.accumulate(0);
        assertThat(accumulator.getValue())
                .isEqualTo(MAX_VALUE);

        accumulator.accumulate(MIN_VALUE);
        assertThat(accumulator.getValue())
                .isEqualTo(-1L);
    }

    @Test
    void shouldAccumulateUntilMin() throws Accumulator.AccumulatorMax, Accumulator.AccumulatorMin {
        final var accumulator = new Accumulator(0);

        assertThat(accumulator.getValue())
                .isEqualTo(0);

        accumulator.accumulate(MIN_VALUE);

        assertThatThrownBy(() -> accumulator.accumulate(-1L))
                .isInstanceOf(Accumulator.AccumulatorMin.class);

        accumulator.accumulate(0);
        assertThat(accumulator.getValue())
                .isEqualTo(MIN_VALUE);

        accumulator.accumulate(1);
        assertThat(accumulator.getValue())
                .isEqualTo(MIN_VALUE + 1);

        accumulator.accumulate(MAX_VALUE);
        assertThat(accumulator.getValue())
                .isEqualTo(0);

        accumulator.accumulate(MAX_VALUE);
        assertThat(accumulator.getValue())
                .isEqualTo(MAX_VALUE);

        accumulator.accumulate(MIN_VALUE);
        assertThat(accumulator.getValue())
                .isEqualTo(-1L);
    }
}