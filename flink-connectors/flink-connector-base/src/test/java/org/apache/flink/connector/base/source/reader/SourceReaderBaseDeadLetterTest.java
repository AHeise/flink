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

package org.apache.flink.connector.base.source.reader;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceOutput;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.mocks.TestingRecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.mocks.TestingSourceSplit;
import org.apache.flink.connector.base.source.reader.mocks.TestingSplitReader;
import org.apache.flink.connector.testutils.source.reader.TestingReaderContext;
import org.apache.flink.connector.testutils.source.reader.TestingReaderOutput;
import org.apache.flink.core.io.InputStatus;
import org.apache.flink.util.DeadLetter;
import org.apache.flink.util.OutputTag;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests {@link SourceReaderBase}'s automatic dead-lettering of records that fail {@code emitRecord}. */
class SourceReaderBaseDeadLetterTest {

    @Test
    void poisonRecordIsRoutedToConnectedDeadLetterOutput() throws Exception {
        final SourceReader<Integer, TestingSourceSplit> reader = poisonReader();
        final RecordingReaderOutput output = new RecordingReaderOutput();

        pollUntilDone(reader, output);

        assertThat(output.deadLetters)
                .singleElement()
                .satisfies(
                        dl -> {
                            assertThat(dl.getRecord()).isEqualTo(42);
                            assertThat(dl.getError().getMessage()).contains("poison");
                        });
    }

    @Test
    void poisonRecordRethrowsWhenNoDeadLetterOutputConnected() throws Exception {
        final SourceReader<Integer, TestingSourceSplit> reader = poisonReader();

        // TestingReaderOutput has no connected side output -> the emit failure must surface.
        assertThatThrownBy(() -> pollUntilDone(reader, new TestingReaderOutput<>()))
                .hasMessageContaining("poison");
    }

    private static SourceReader<Integer, TestingSourceSplit> poisonReader() throws Exception {
        final TestingRecordsWithSplitIds<Integer> records =
                new TestingRecordsWithSplitIds<>("split-0", 42);
        final RateLimiterStrategy noRateLimit = null;
        final SourceReader<Integer, TestingSourceSplit> reader =
                new SingleThreadMultiplexSourceReaderBase<
                        Integer, Integer, TestingSourceSplit, TestingSourceSplit>(
                        () -> new TestingSplitReader<>(records),
                        (element, out, splitState) -> {
                            throw new IOException("poison-" + element);
                        },
                        new Configuration(),
                        new TestingReaderContext(),
                        noRateLimit) {
                    @Override
                    protected void onSplitFinished(
                            Map<String, TestingSourceSplit> finishedSplitIds) {}

                    @Override
                    protected TestingSourceSplit initializedState(TestingSourceSplit split) {
                        return split;
                    }

                    @Override
                    protected TestingSourceSplit toSplitType(
                            String splitId, TestingSourceSplit splitState) {
                        return splitState;
                    }
                };
        reader.start();
        reader.addSplits(Collections.singletonList(new TestingSourceSplit("split-0")));
        reader.notifyNoMoreSplits();
        return reader;
    }

    private static void pollUntilDone(
            SourceReader<Integer, TestingSourceSplit> reader, ReaderOutput<Integer> output)
            throws Exception {
        while (true) {
            final InputStatus status = reader.pollNext(output);
            if (status == InputStatus.END_OF_INPUT) {
                return;
            }
            if (status == InputStatus.NOTHING_AVAILABLE) {
                reader.isAvailable().get();
            }
        }
    }

    private static final class RecordingReaderOutput implements ReaderOutput<Integer> {
        private final List<DeadLetter<?>> deadLetters = new ArrayList<>();

        @Override
        public void collect(Integer record) {}

        @Override
        public void collect(Integer record, long timestamp) {}

        @Override
        public <X> void collect(OutputTag<X> outputTag, X value) {
            if (value instanceof DeadLetter) {
                deadLetters.add((DeadLetter<?>) value);
            }
        }

        @Override
        public <X> void collect(OutputTag<X> outputTag, X value, long timestamp) {
            collect(outputTag, value);
        }

        @Override
        public void emitWatermark(Watermark watermark) {}

        @Override
        public void markIdle() {}

        @Override
        public void markActive() {}

        @Override
        public SourceOutput<Integer> createOutputForSplit(String splitId) {
            return this;
        }

        @Override
        public void releaseOutputForSplit(String splitId) {}
    }
}
