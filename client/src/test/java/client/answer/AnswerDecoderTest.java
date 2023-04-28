package client.answer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.shadow.com.univocity.parsers.common.ArgumentUtils.toByteArray;

class AnswerDecoderTest {

    private final AnswerDecoder answerDecoder = new AnswerDecoder();

    @Test
    void shouldDecodeType16() {
        final byte[] answer = toByteArray(255, 255, 255, 255, 255, 255, 253, 243);

        assertThat(answerDecoder.decodeNumber16(answer))
                .isEqualTo("Accumulator: [-525]");
    }

    @Test
    void shouldDecodeType16WithValue() {
        byte[] answer = toByteArray(10, 10, 16, 8, 255, 255, 255, 255, 255, 255, 253, 243);

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Message: [Accumulator: [-525]].");
    }

    @Test
    void shouldDecodeType10WithContent() {
        byte[] answer = toByteArray(
                10, 31, 11, 19, 67, 97, 110, 32, 110, 111, 116, 32, 100, 105, 118, 105, 100,
                101, 32, 98, 121, 32, 48, 16, 8, 0, 0, 0, 0, 0, 0, 0, 5
        );

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Message: [Text: [Can not divide by 0], Accumulator: [5]].");
    }

    @Test
    void shouldDecodeType10WithContentSecondOption() {
        byte[] answer = toByteArray(
                10, 31, 16, 8, 0, 0, 0, 0, 0, 0, 0, 5, 11, 19, 67, 97, 110, 32, 110, 111, 116,
                32, 100, 105, 118, 105, 100, 101, 32, 98, 121, 32, 48
        );

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Message: [Accumulator: [5], Text: [Can not divide by 0]].");
    }

    @Test
    void shouldGoRecursively() {
        byte[] answer = toByteArray(
                10, 31,
                11, 2, 97, 98,
                16, 8, 0, 0, 0, 0, 0, 0, 0, 12,
                16, 8, 0, 0, 0, 0, 0, 0, 0, 5,
                11, 2, 99, 100,
                11, 1, 101
        );

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Message: [Text: [ab], Accumulator: [12], Accumulator: [5], Text: [cd], Text: [e]].");
    }

    @Test
    void shouldRunText() {
        byte[] answer = toByteArray(
                10, 4,
                11, 2, 97, 98
        );

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Message: [Text: [ab]].");
    }

    @Test
    void shouldRunOnlyText() {
        byte[] answer = toByteArray(
                11, 2, 97, 98
        );

        assertThat(answerDecoder.decodeVariable(answer))
                .isEqualTo("Text: [ab]");
    }
}