package nu.mine.mosher.util;

import java.nio.charset.Charset;

public class ShowCharset {
    public static void main(String... args) {
        System.out.println(Charset.defaultCharset().name());
        System.out.flush();
    }
}
