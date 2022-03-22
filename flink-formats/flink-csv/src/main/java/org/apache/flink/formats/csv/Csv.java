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

package org.apache.flink.formats.csv;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.formats.common.Converter;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Csv {

    /**
     * Builds a new {@code CsvReaderFormat} using a {@code CsvSchema}.
     *
     * @param schema The Jackson CSV schema configured for parsing specific CSV files.
     * @param typeInformation The Flink type descriptor of the returned elements.
     * @param <T> The type of the returned elements.
     */
    public static <T> CsvReaderFormat<T> readerForSchema(
            CsvSchema schema, TypeInformation<T> typeInformation) {
        return readerForSchema(new CsvMapper(), schema, typeInformation);
    }

    /**
     * Builds a new {@code CsvReaderFormat} using a {@code CsvSchema} and a pre-created {@code
     * CsvMapper}.
     *
     * @param mapper The pre-created {@code CsvMapper}.
     * @param schema The Jackson CSV schema configured for parsing specific CSV files.
     * @param typeInformation The Flink type descriptor of the returned elements.
     * @param <T> The type of the returned elements.
     */
    public static <T> CsvReaderFormat<T> readerForSchema(
            CsvMapper mapper, CsvSchema schema, TypeInformation<T> typeInformation) {
        return new CsvReaderFormat<>(
                mapper,
                schema,
                typeInformation.getTypeClass(),
                (value, context) -> value,
                typeInformation,
                false,
                StandardCharsets.UTF_8.name());
    }

    /**
     * Builds a new {@code CsvReaderFormat} for reading CSV files mapped to the provided POJO class
     * definition. Produced reader uses default mapper and schema settings, use {@code forSchema} if
     * you need customizations.
     *
     * @param pojoType The type class of the POJO.
     * @param <T> The type of the returned elements.
     */
    public static <T> CsvReaderFormat<T> readerForPojo(Class<T> pojoType) {
        CsvMapper mapper = new CsvMapper();
        return readerForSchema(
                mapper,
                mapper.schemaFor(pojoType).withoutQuoteChar(),
                TypeInformation.of(pojoType));
    }

    /**
     * Builds a new {@code CsvReaderFormat} using a {@code CsvSchema}.
     *
     * @param schema The Jackson CSV schema configured for parsing specific CSV files.
     * @param typeInformation The Flink type descriptor of the returned elements.
     * @param <T> The type of the returned elements.
     */
    public static <T> WriterFactory<T> writerForSchema(CsvSchema schema) {
        return writerForSchema(new CsvMapper(), schema);
    }

    /**
     * Builds a new {@code CsvReaderFormat} using a {@code CsvSchema} and a pre-created {@code
     * CsvMapper}.
     *
     * @param mapper The pre-created {@code CsvMapper}.
     * @param schema The Jackson CSV schema configured for parsing specific CSV files.
     * @param typeInformation The Flink type descriptor of the returned elements.
     * @param <T> The type of the returned elements.
     */
    public static <T> WriterFactory<T> writerForSchema(CsvMapper mapper, CsvSchema schema) {
        return new WriterFactory<>(
                mapper, schema, (value, context) -> value, null, StandardCharsets.UTF_8.name());
    }

    /**
     * Builds a new {@code CsvReaderFormat} for reading CSV files mapped to the provided POJO class
     * definition. Produced reader uses default mapper and schema settings, use {@code forSchema} if
     * you need customizations.
     *
     * @param pojoType The type class of the POJO.
     * @param <T> The type of the returned elements.
     */
    public static <T> WriterFactory<T> writerForPojo(Class<T> pojoType) {
        final Converter<T, T, Void> converter = (value, context) -> value;
        final CsvMapper csvMapper = new CsvMapper();
        final CsvSchema schema = csvMapper.schemaFor(pojoType).withoutQuoteChar();
        return new WriterFactory<>(
                csvMapper, schema, converter, null, StandardCharsets.UTF_8.name());
    }

    public static class WriterFactory<T> implements BulkWriter.Factory<T> {

        private final CsvMapper mapper;
        private final CsvSchema schema;
        private final Converter<T, Object, Object> converter;
        private final Object converterContext;
        private final String charset;

        private <R, C> WriterFactory(
                CsvMapper mapper,
                CsvSchema schema,
                Converter<T, R, C> converter,
                C converterContext,
                String charset) {
            this.mapper = mapper;
            this.schema = schema;
            this.converter = (Converter<T, Object, Object>) converter;
            this.converterContext = converterContext;
            this.charset = charset;
        }

        /** Returns a new {@code CsvReaderFormat} with the given charset. */
        public WriterFactory<T> withCharset(Charset charset) {
            return withCharset(charset.name());
        }

        /** Returns a new {@code CsvReaderFormat} with the given charset. */
        public WriterFactory<T> withCharset(String charset) {
            return new WriterFactory<>(
                    this.mapper, this.schema, this.converter, this.converterContext, charset);
        }

        @Override
        public BulkWriter<T> create(FSDataOutputStream out) throws IOException {
            return new CsvBulkWriter<>(mapper, schema, converter, converterContext, out, charset);
        }
    }
}
