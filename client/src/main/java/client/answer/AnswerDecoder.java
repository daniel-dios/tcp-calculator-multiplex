package client.answer;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AnswerDecoder {
    public String decode(final byte[] buffer) {
        return decodeNumber16(Arrays.copyOfRange(buffer, 2, buffer.length));
    }

    public String decodeVariable(final byte[] buffer) {
        if (buffer.length > 2) {
            final var type = buffer[0];
            final var size = buffer[1];
            final var value = Arrays.copyOfRange(buffer, 2, 2 + size);
            switch (type) {
                case 10:
                    return decode10(value);
                case 11:
                    return decodeText11(value);
                case 16:
                    return decodeNumber16(value);
            }
        }
        return "Unsupported answer from server.";
    }

    public String decodeNumber16(final byte[] buffer) {
        return "Accumulator: [" + ByteBuffer
                .wrap(Arrays.copyOfRange(buffer, 0, buffer.length))
                .getLong() + "]";
    }

    private String decodeText11(final byte[] value) {
        try {
            return "Text: [" + StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(value)) + "]";
        } catch (IllegalStateException | CharacterCodingException e) {
            return "Message is not UTF encoded";
        }
    }

    private String decode10(final byte[] data) {
        // [11,19,67,97,110,32,110,111,116,32,100,105,118,105,100,101,32,98,121,32,48,16,8,0,0,0,0,0,0,0,5]
        // 31 --> [11,19,67,97,110,32,110,111,116,32,100,105,118,105,100,101,32,98,121,32,48,16,8,0,0,0,0,0,0,0,5]
        // 31 --> [T,19,67,97,110,32,110,111,116,32,100,105,118,105,100,101,32,98,121,32,48,16,8,0,0,0,0,0,0,0,5]
        // 19 --> [T,19,-  ,-  ,-  ,-  ,-  ,-  ,-  ,-  ,-  ,-  ,-  ,-  ,- ,-,-,-,- ,-  ,-  ,16,8,0,0,0,0,0,0,0,5]

        final var builder = new StringBuilder();
        builder.append("Message: [");
        for (int i = 0; i < data.length; ) {
            if (i != 0) {
                builder.append(", ");
            }
            var value = data[i];
            var size = data[i + 1];
            var copy = Arrays.copyOfRange(data, i, i + 2 + size);

            if (value == 11 || value == 16) {
                builder.append(decodeVariable(copy)); // recursive
            }
            i = i + 2 + size;
        }
        return builder.append("].").toString();
    }
}
