package nu.mine.mosher.util;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Objects;

import static java.nio.charset.CoderResult.OVERFLOW;
import static java.nio.charset.CoderResult.UNDERFLOW;

public final class DebugCharset extends Charset {
    public static final String CANONICAL_NAME = "X-DEBUG";
    public static final byte BAD_BYTE = (byte) 0x95;

    public DebugCharset() {
        super(CANONICAL_NAME, null);
    }

    @Override
    public boolean contains(final Charset other) {
        return Objects.requireNonNull(other) instanceof DebugCharset;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new CharsetDecoder(this, 1.0f, 1.0f) {
            @Override
            protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
                int mark = in.position();
                try {
                    while (in.hasRemaining()) {
                        in.get(); // get it, but ignore it
                        if (!out.hasRemaining()) {
                            return OVERFLOW;
                        }
                        out.put(this.replacement());
                        ++mark;
                    }
                    return UNDERFLOW;
                } finally {
                    in.position(mark);
                }
            }
        };
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new CharsetEncoder(this, 1.0f, 1.0f) {
            @Override
            protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
                int mark = in.position();
                try {
                    while (in.hasRemaining()) {
                        in.get(); // get it, but ignore it
                        if (!out.hasRemaining()) {
                            return OVERFLOW;
                        }
                        out.put(BAD_BYTE);
                        ++mark;
                    }
                    return UNDERFLOW;
                } finally {
                    in.position(mark);
                }
            }
        };
    }


    public static void main(final String... args) throws NoSuchFieldException, IllegalAccessException {
        final Charset charset = Charset.forName(CANONICAL_NAME);
        System.out.println("Acquired Charset named: " + charset.name());
        System.out.println();

        final String data = "Data String";
        System.out.println("Using test string: " + data + " (length: " + data.length() + " characters)");

        final byte[] bytes = data.getBytes(charset);
        System.out.println("Encoded to " + bytes.length + " bytes:");
        for (byte b : bytes) {
            System.out.printf("  %02X", b);
        }
        System.out.println();
        System.out.println("Which would look like this if decoded using Windows-1252:");
        System.out.println(new String(bytes, Charset.forName("Windows-1252")));

        System.out.println();

        final byte[] bData = new byte[]{(byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44};
        System.out.println("Using " + bData.length + " test bytes:");
        for (byte b : bData) {
            System.out.printf("  0x%02X", b);
        }
        System.out.println();

        final String s = new String(bData, charset);
        System.out.println("Decoded to " + s.length() + " characters:");
        System.out.println(s);
        s.codePoints().forEach(cp -> System.out.printf("  0x%08X", cp));
        System.out.println();

        System.out.println();

        System.out.println("Setting default Charset to: "+charset.name());
        final Field dc = Charset.class.getDeclaredField("defaultCharset");
        dc.setAccessible(true);
        dc.set(null, charset);

        final Charset defaultCharset = Charset.defaultCharset();
        System.out.println("Using default Charset: "+defaultCharset.name());
        final String sD = new String(bData, defaultCharset);
        System.out.println("Decoded to " + sD.length() + " characters:");
        System.out.println(sD);
        sD.codePoints().forEach(cp -> System.out.printf("  0x%08X", cp));
        System.out.println();

        System.out.flush();
    }
}
