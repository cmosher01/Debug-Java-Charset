package nu.mine.mosher.util;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class DebugCharsetProvider extends CharsetProvider {
    @Override
    public Iterator<Charset> charsets() {
        return Collections.<Charset>singleton(new DebugCharset()).iterator();
    }

    @Override
    public Charset charsetForName(final String name) {
        return Objects.requireNonNull(name).equals(DebugCharset.CANONICAL_NAME) ? new DebugCharset() : null;
    }
}
