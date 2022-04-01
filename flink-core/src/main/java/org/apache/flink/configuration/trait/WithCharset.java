package org.apache.flink.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link Configurable} that allows users setting a {@link Charset}.
 */
public interface WithCharset<SELF extends Configurable<SELF>> extends Configurable<SELF> {

    ConfigOption<String> CHARSET =
            ConfigOptions.key("charset")
                    .stringType()
                    .defaultValue(StandardCharsets.UTF_8.displayName())
                    .withDescription("Defines the string charset.");

    /** Sets the charset and returns this. */
    default SELF withCharset(Charset charset) {
        return withCharset(charset.name());
    }

    /** Sets the charset and returns this. */
    default SELF withCharset(String charset) {
        return withOption(CHARSET, charset);
    }
}
