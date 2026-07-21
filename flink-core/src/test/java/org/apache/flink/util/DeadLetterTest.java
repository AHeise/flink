/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.util;

import org.apache.flink.api.common.serialization.SerializerConfigImpl;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests for {@link DeadLetter}'s TypeInformation and serialization. */
class DeadLetterTest {

    @Test
    void typeInfoRoundTripsRecordAndError() throws Exception {
        final TypeInformation<DeadLetter<String>> typeInfo = DeadLetter.typeInfo(Types.STRING);
        final TypeSerializer<DeadLetter<String>> serializer =
                typeInfo.createSerializer(new SerializerConfigImpl());

        final DeadLetter<String> original = DeadLetter.of("payload", new RuntimeException("boom"));
        final DataOutputSerializer out = new DataOutputSerializer(128);
        serializer.serialize(original, out);
        final DeadLetter<String> copy =
                serializer.deserialize(new DataInputDeserializer(out.getCopyOfBuffer()));

        assertThat(copy.getRecord()).isEqualTo("payload");
        assertThat(copy.getError()).isNotNull();
        assertThat(copy.getError().getMessage()).contains("boom");
    }

    @Test
    void typeHintResolvesViaFactory() {
        final TypeInformation<DeadLetter<String>> typeInfo =
                TypeInformation.of(new TypeHint<DeadLetter<String>>() {});
        assertThat(typeInfo).isInstanceOf(PojoTypeInfo.class);
    }
}
