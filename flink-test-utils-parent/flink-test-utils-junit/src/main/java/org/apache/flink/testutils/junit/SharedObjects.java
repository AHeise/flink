/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.testutils.junit;

import org.junit.rules.ExternalResource;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This rule allows objects to be used both in the main test case as well as in UDFs by using
 * serializable {@link SharedReference}s. Usage:
 *
 * <pre><code>
 * {@literal    @Rule}
 *     public final SharedObjects sharedObjects = SharedObjects.create();
 *
 * {@literal    @Test}
 *     public void test() throws Exception {
 *         StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
 *         SerialRef<Queue<Long>> list = sharedObjects.add(new ConcurrentLinkedQueue<>());
 *         int n = 10000;
 *         env.setParallelism(100);
 *         env.fromSequence(0, n).map(i -> list.get().add(i));
 *         env.execute();
 *         assertEquals(n + 1, list.get().size());
 *         assertEquals(
 *                 LongStream.rangeClosed(0, n).boxed().collect(Collectors.toList()),
 *                 list.get().stream().sorted().collect(Collectors.toList()));
 *     }
 * </code></pre>
 *
 * <p>Note that since the shared objects are accessed through multiple threads, they need to be thread-safe or accessed in a thread-safe manner.
 */
public class SharedObjects extends ExternalResource implements Serializable {
    /**
     * Instance-cache used to make a SharedObjects accessible for multiple threads.
     */
    private static final Map<Integer, SharedObjects> INSTANCES = new ConcurrentHashMap<>();
    private static final AtomicInteger LAST_ID = new AtomicInteger();
    /**
     * Identifier of the SharedObjects used to retrieve the original instance during deserialization.
     */
    private final int id;
    /**
     * All registered objects for the current test case. The objects are purged upon completion.
     */
    private final transient Map<SharedReference<?>, Object> objects = new ConcurrentHashMap<>();

    private SharedObjects(int id) {
        this.id = id;
    }

    /**
     * Creates a new instance. Usually that should be done inside a JUnit test class as an
     * instance-field annotated with {@link org.junit.Rule}.
     */
    public static SharedObjects create() {
        return new SharedObjects(LAST_ID.getAndIncrement());
    }

    /**
     * Adds a new object to this {@code SharedObjects}. Although not necessary, it is recommended
     * to only access the object through the returned {@link SharedReference}.
     */
    public <T> SharedReference<T> add(T object) {
        SharedReference<T> tag = new DefaultTag<>(this, objects.size());
        objects.put(tag, object);
        return tag;
    }

    @Override
    protected void before() throws Throwable {
        INSTANCES.put(id, this);
    }

    @Override
    protected void after() {
        objects.clear();
        INSTANCES.remove(id);
    }

    private Object readResolve() throws ObjectStreamException {
        // replace the deserialized copy of SharedObjects with the original instance
        return INSTANCES.get(id);
    }

    @SuppressWarnings("unchecked")
    <T> T get(SharedReference<T> tag) {
        T object = (T) objects.get(tag);
        if (object == null) {
            throw new IllegalStateException("Object was accessed after the test was completed");
        }
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedObjects that = (SharedObjects) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static class DefaultTag<T> implements SharedReference<T> {
        private final SharedObjects sharedObjects;
        private final int objectId;

        public DefaultTag(SharedObjects sharedObjects, int objectId) {
            this.sharedObjects = sharedObjects;
            this.objectId = objectId;
        }

        @Override
        public T get() {
            return sharedObjects.get(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DefaultTag<?> that = (DefaultTag<?>) o;
            return objectId == that.objectId && Objects.equals(sharedObjects, that.sharedObjects);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sharedObjects, objectId);
        }
    }
}
