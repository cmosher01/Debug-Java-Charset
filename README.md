# Debug-Java-Charset

A `Charset` for Java that decodes every byte to U+FFFD,
and encodes every character to a `0x95` byte.

Set it as the default charset to help flush out bugs
in code that shouldn't be relying on the default charset.

`Charset.forName("X-DEBUG")`

