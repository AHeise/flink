package org.apache.flink.api.common.serialization;

import static org.apache.flink.util.Preconditions.checkNotNull;

import java.util.function.Consumer;

import org.apache.flink.configuration.Configuration;

public abstract class AbstractConfigurable<SELF extends AbstractConfigurable<SELF>> implements Configurable<SELF> {
    protected final Configuration configuration;

    protected AbstractConfigurable(Configuration configuration) {
        this.configuration = checkNotNull(configuration);
    }

    protected AbstractConfigurable() {
        this(new Configuration());
    }

    @Override
    public SELF configure(
            Consumer<Configuration> configurationConsumer) {
        configurationConsumer.accept(configuration);
        return self();
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }
}
