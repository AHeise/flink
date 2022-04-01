package org.apache.flink.api.common.serialization;

import java.util.Collections;
import java.util.Set;

import java.util.function.Consumer;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;

public interface Configurable<SELF extends Configurable<SELF>> {
    @SuppressWarnings("unchecked")
    default <T> SELF configure(ConfigOption<T> option, T value) {
        configure(configuration -> configuration.set(option, value));
        return (SELF) this;
    }

    SELF configure(Consumer<Configuration> configurationConsumer);

    default SELF apply(Configuration otherConfiguration) {
        configure(configuration -> configuration.addAll(otherConfiguration));
        return (SELF) this;
    }

    /**
     * Returns a set of {@link ConfigOption} that an implementation of this factory requires in
     * addition to {@link #optionalOptions()}.
     */
    default Set<ConfigOption<?>> requiredOptions() {
        return Collections.emptySet();
    }

    /**
     * Returns a set of {@link ConfigOption} that an implementation of this factory consumes in
     * addition to {@link #requiredOptions()}.
     */
    default Set<ConfigOption<?>> optionalOptions() {
        return Collections.emptySet();
    }
}
