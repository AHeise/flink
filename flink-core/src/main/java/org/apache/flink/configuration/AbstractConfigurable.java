package org.apache.flink.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Base class for {@link Configurable}s that automates option discovery.
 *
 * @param <SELF> the specific implementing class for using the {@code withX} methods. Users
 *         can simply use a wildcard {@code AbstractConfigurable<?>} after full configuration.
 */
public abstract class AbstractConfigurable<SELF extends AbstractConfigurable<SELF>> implements
        Configurable<SELF> {

    /** The log object used for debugging. */
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    protected final Configuration configuration;
    protected final Set<ConfigOption<?>> optionalOptions;
    protected final Set<ConfigOption<?>> requiredOptions;

    protected AbstractConfigurable(
            Configuration configuration,
            Set<ConfigOption<?>> optionalOptions,
            Set<ConfigOption<?>> requiredOptions) {
        this.configuration = checkNotNull(configuration);
        this.optionalOptions = optionalOptions;
        this.requiredOptions = requiredOptions;
    }

    protected AbstractConfigurable(
            Configuration configuration, Class<?> selfClass) {
        this(configuration, inferOptions(selfClass, true), inferOptions(selfClass, false));
    }

    protected AbstractConfigurable() {
        this(new Configuration(), new HashSet<>(), new HashSet<>());
    }

    protected AbstractConfigurable(Class<?> selfClass) {
        this(new Configuration(), inferOptions(selfClass, true), inferOptions(selfClass, false));
    }

    private static Set<ConfigOption<?>> inferOptions(Class<?> clazz, boolean optional) {
        return OptionExtractor.INSTANCE
                .getOptions(clazz)
                .stream()
                .filter(o -> o.hasDefaultValue() == optional)
                .collect(Collectors.toSet());
    }

    @Override
    public <T> SELF withOption(ConfigOption<T> option, T value) {
        configuration.set(option, value);
        return self();
    }

    public SELF withOptions(Configuration otherConfiguration) {
        configuration.addAll(otherConfiguration);
        return self();
    }

    public SELF withModifiedConfiguration(
            Consumer<Configuration> configurationConsumer) {
        configurationConsumer.accept(configuration);
        return self();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return optionalOptions;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return requiredOptions;
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    @ThreadSafe
    static class OptionExtractor {

        static final OptionExtractor INSTANCE = new OptionExtractor();

        private final Map<Class<?>, List<ConfigOption<?>>> inferredOptions = new ConcurrentHashMap<>();

        private List<ConfigOption<?>> getOptions(
                Class<?> clazz) {
            return inferredOptions.computeIfAbsent(
                    clazz,
                    this::getOptionInHierarchy);
        }

        private List<ConfigOption<?>> getOptionInHierarchy(Class<?> clazz) {
            Stream<ConfigOption<?>> superOptions = Stream
                    .concat(Arrays.stream(clazz.getInterfaces()), Stream.of(clazz.getSuperclass()))
                    .flatMap(c -> getOptions(c).stream());
            return Stream.concat(getOptionFields(clazz), superOptions).collect(Collectors.toList());
        }

        private Stream<ConfigOption<?>> getOptionFields(Class<?> clazz) {
            return Arrays
                    .stream(clazz.getFields())
                    .filter(f -> Modifier.isPublic(f.getModifiers())
                            && Modifier.isStatic(f.getModifiers())
                            && ConfigOption.class.isAssignableFrom(f.getType()))
                    .map(f -> {
                        try {
                            return f.get(null);
                        } catch (IllegalAccessException e) {
                            LOG.error("Cannot access {}: {}", f, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .map(option -> (ConfigOption<?>) option);
        }
    }
}
