/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.util;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Map.entry;

/**
 * A record that could not be processed, paired with the failure that rejected it. Emitted to an
 * error side output (a "dead-letter queue") instead of failing the job.
 *
 * @param <R> the type of the rejected record.
 */
@PublicEvolving
@TypeInfo(DeadLetter.DeadLetterTypeInfoFactory.class)
public class DeadLetter<R> {

    /**
     * Id of the blessed source error side output (see {@code DataStreamSource#getErrorSideOutput}).
     */
    public static final String SOURCE_TAG_ID = "source-errors";

    private R record;
    private SerializedThrowable error;

    public DeadLetter() {}

    public DeadLetter(R record, SerializedThrowable error) {
        this.record = record;
        this.error = error;
    }

    /**
     * Wraps a rejected record and its cause; the cause is stored as a {@link SerializedThrowable}.
     */
    public static <R> DeadLetter<R> of(R record, Throwable cause) {
        return new DeadLetter<>(record, new SerializedThrowable(cause));
    }

    public R getRecord() {
        return record;
    }

    public void setRecord(R record) {
        this.record = record;
    }

    public SerializedThrowable getError() {
        return error;
    }

    public void setError(SerializedThrowable error) {
        this.error = error;
    }

    /**
     * Builds the {@link TypeInformation} of a {@code DeadLetter} carrying the given record type.
     */
    @SuppressWarnings("unchecked")
    public static <R> TypeInformation<DeadLetter<R>> typeInfo(TypeInformation<R> recordType) {
        return (TypeInformation<DeadLetter<R>>)
                (TypeInformation<?>)
                        Types.POJO(
                                DeadLetter.class,
                                Map.ofEntries(
                                        entry("record", recordType),
                                        entry(
                                                "error",
                                                TypeInformation.of(SerializedThrowable.class))));
    }

    /**
     * {@link TypeInformation} for a {@code DeadLetter} whose record type is unknown (Kryo record).
     */
    public static TypeInformation<DeadLetter<Object>> genericTypeInfo() {
        return typeInfo(TypeInformation.of(Object.class));
    }

    /** Resolves {@code DeadLetter}'s {@link TypeInformation} from a {@code TypeHint}. */
    public static final class DeadLetterTypeInfoFactory<R> extends TypeInfoFactory<DeadLetter<R>> {
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public TypeInformation<DeadLetter<R>> createTypeInfo(
                Type t, Map<String, TypeInformation<?>> genericParameters) {
            TypeInformation<?> recordType = genericParameters.get("R");
            if (recordType == null) {
                recordType = TypeInformation.of(Object.class);
            }
            return DeadLetter.typeInfo((TypeInformation) recordType);
        }
    }
}
