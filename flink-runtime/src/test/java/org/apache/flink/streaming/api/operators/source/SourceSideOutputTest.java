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

package org.apache.flink.streaming.api.operators.source;

import org.apache.flink.api.common.eventtime.RecordTimestampAssigner;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.runtime.event.WatermarkEvent;
import org.apache.flink.runtime.metrics.groups.InternalSourceReaderMetricGroup;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.runtime.io.PushingAsyncDataInput;
import org.apache.flink.streaming.runtime.streamrecord.LatencyMarker;
import org.apache.flink.streaming.runtime.streamrecord.RecordAttributes;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.runtime.tasks.SourceOperatorStreamTask.AsyncDataOutputToOutput;
import org.apache.flink.streaming.runtime.watermarkstatus.WatermarkStatus;
import org.apache.flink.util.ErrorOutputTag;
import org.apache.flink.util.OutputTag;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for source side-output emission via {@link
 * org.apache.flink.api.connector.source.SourceOutput}.
 */
class SourceSideOutputTest {

    private static final OutputTag<String> ERROR = new ErrorOutputTag<>("dlq", Types.STRING);
    private static final OutputTag<String> LATE = new OutputTag<>("late", Types.STRING);

    @Test
    void sideOutputRoutesToTaggedOutputAndAdvancesWatermarkWithNullEvent() {
        final RecordingDataOutput<Integer> dataOutput = new RecordingDataOutput<>();
        final RecordingWatermarkOutput watermarks = new RecordingWatermarkOutput();

        // A generator that emits the raw timestamp as a watermark and never inspects the event,
        // so passing a null event for a diverted record must still advance the watermark.
        final WatermarkGenerator<Integer> generator =
                new WatermarkGenerator<Integer>() {
                    @Override
                    public void onEvent(Integer event, long eventTimestamp, WatermarkOutput out) {
                        out.emitWatermark(new Watermark(eventTimestamp));
                    }

                    @Override
                    public void onPeriodicEmit(WatermarkOutput out) {}
                };

        final SourceOutputWithWatermarks<Integer> output =
                SourceOutputWithWatermarks.createWithSeparateOutputs(
                        dataOutput,
                        watermarks,
                        watermarks,
                        new RecordTimestampAssigner<>(),
                        generator);

        output.collect(1, 100L);
        output.collect(ERROR, "bad-record", 200L);

        assertThat(dataOutput.mainValues).containsExactly(1);
        assertThat(dataOutput.sideValues).containsExactly("bad-record");
        assertThat(dataOutput.lastSideTag).isEqualTo(ERROR);
        assertThat(watermarks.timestamps).containsExactly(100L, 200L);
    }

    @Test
    void errorOutputTagIncrementsErrorMetricButGenericTagDoesNot() throws Exception {
        final RecordingOutput<Integer> output = new RecordingOutput<>();
        final InternalSourceReaderMetricGroup metricGroup =
                InternalSourceReaderMetricGroup.mock(
                        UnregisteredMetricGroups.createUnregisteredOperatorMetricGroup());
        final AsyncDataOutputToOutput<Integer> adapter =
                new AsyncDataOutputToOutput<>(output, metricGroup, null);

        adapter.emitRecord(ERROR, new StreamRecord<>("bad-record", 1L));
        assertThat(metricGroup.getNumRecordsInErrorsCounter().getCount()).isEqualTo(1L);

        adapter.emitRecord(LATE, new StreamRecord<>("late-record", 2L));
        assertThat(metricGroup.getNumRecordsInErrorsCounter().getCount()).isEqualTo(1L);

        assertThat(output.sideValues).containsExactly("bad-record", "late-record");
        assertThat(output.sideTags).containsExactly(ERROR, LATE);
    }

    private static final class RecordingDataOutput<T>
            implements PushingAsyncDataInput.DataOutput<T> {
        private final List<T> mainValues = new ArrayList<>();
        private final List<Object> sideValues = new ArrayList<>();
        private OutputTag<?> lastSideTag;

        @Override
        public void emitRecord(StreamRecord<T> streamRecord) {
            mainValues.add(streamRecord.getValue());
        }

        @Override
        public <X> void emitRecord(OutputTag<X> outputTag, StreamRecord<X> streamRecord) {
            lastSideTag = outputTag;
            sideValues.add(streamRecord.getValue());
        }

        @Override
        public void emitWatermark(org.apache.flink.streaming.api.watermark.Watermark watermark) {}

        @Override
        public void emitWatermarkStatus(WatermarkStatus watermarkStatus) {}

        @Override
        public void emitLatencyMarker(LatencyMarker latencyMarker) {}

        @Override
        public void emitRecordAttributes(RecordAttributes recordAttributes) {}

        @Override
        public void emitWatermark(WatermarkEvent watermark) {}
    }

    private static final class RecordingWatermarkOutput implements WatermarkOutput {
        private final List<Long> timestamps = new ArrayList<>();

        @Override
        public void emitWatermark(Watermark watermark) {
            timestamps.add(watermark.getTimestamp());
        }

        @Override
        public void markIdle() {}

        @Override
        public void markActive() {}
    }

    private static final class RecordingOutput<T> implements Output<StreamRecord<T>> {
        private final List<Object> sideValues = new ArrayList<>();
        private final List<OutputTag<?>> sideTags = new ArrayList<>();

        @Override
        public <X> void collect(OutputTag<X> outputTag, StreamRecord<X> record) {
            sideTags.add(outputTag);
            sideValues.add(record.getValue());
        }

        @Override
        public void collect(StreamRecord<T> record) {}

        @Override
        public void emitWatermark(org.apache.flink.streaming.api.watermark.Watermark mark) {}

        @Override
        public void emitWatermarkStatus(WatermarkStatus watermarkStatus) {}

        @Override
        public void emitLatencyMarker(LatencyMarker latencyMarker) {}

        @Override
        public void emitRecordAttributes(RecordAttributes recordAttributes) {}

        @Override
        public void emitWatermark(WatermarkEvent watermark) {}

        @Override
        public void close() {}
    }
}
