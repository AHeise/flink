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

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.core.io.InputStatus;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamEdge;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.streaming.api.graph.StreamNode;
import org.apache.flink.util.ErrorOutputTag;
import org.apache.flink.util.OutputTag;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link DataStreamSource#getSideOutput} on a FLIP-27 source produces a side-output
 * edge off the source node, with no changes to the source transformation translator.
 */
class SourceSideOutputTranslationTest {

    private static final OutputTag<String> ERROR = new ErrorOutputTag<>("dlq", Types.STRING);

    @Test
    void getSideOutputOnSourceProducesTaggedEdgeOffSourceNode() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        final DataStreamSource<Integer> source =
                env.fromSource(new IntSource(6), WatermarkStrategy.noWatermarks(), "int-source");
        final SideOutputDataStream<String> errors = source.getSideOutput(ERROR);
        errors.print();

        final StreamGraph streamGraph = env.getStreamGraph();

        final List<StreamEdge> taggedEdges = new ArrayList<>();
        for (StreamNode node : streamGraph.getStreamNodes()) {
            for (StreamEdge edge : node.getOutEdges()) {
                if (edge.getOutputTag() != null) {
                    taggedEdges.add(edge);
                }
            }
        }

        assertThat(taggedEdges).hasSize(1);
        final StreamEdge sideEdge = taggedEdges.get(0);
        assertThat(sideEdge.getOutputTag().getId()).isEqualTo("dlq");
        // The side-output edge originates from the source operator.
        assertThat(streamGraph.getStreamNode(sideEdge.getSourceId()).getOperatorName())
                .contains("Source");
    }

    /** A bounded source emitting {@code 0..count-1}; even to main, odd to the error side output. */
    private static final class IntSource implements Source<Integer, IntSplit, Void> {
        private final int count;

        private IntSource(int count) {
            this.count = count;
        }

        @Override
        public Boundedness getBoundedness() {
            return Boundedness.BOUNDED;
        }

        @Override
        public SplitEnumerator<IntSplit, Void> createEnumerator(
                SplitEnumeratorContext<IntSplit> enumContext) {
            return new IntEnumerator(enumContext, count);
        }

        @Override
        public SplitEnumerator<IntSplit, Void> restoreEnumerator(
                SplitEnumeratorContext<IntSplit> enumContext, Void checkpoint) {
            return new IntEnumerator(enumContext, count);
        }

        @Override
        public SimpleVersionedSerializer<IntSplit> getSplitSerializer() {
            return new IntSplitSerializer();
        }

        @Override
        public SimpleVersionedSerializer<Void> getEnumeratorCheckpointSerializer() {
            return new VoidSerializer();
        }

        @Override
        public SourceReader<Integer, IntSplit> createReader(SourceReaderContext readerContext) {
            return new IntReader();
        }
    }

    private static final class IntSplit implements SourceSplit {
        private final int from;
        private final int to;

        private IntSplit(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String splitId() {
            return "int-split";
        }
    }

    private static final class IntEnumerator implements SplitEnumerator<IntSplit, Void> {
        private final SplitEnumeratorContext<IntSplit> context;
        private final int count;

        private IntEnumerator(SplitEnumeratorContext<IntSplit> context, int count) {
            this.context = context;
            this.count = count;
        }

        @Override
        public void start() {}

        @Override
        public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {}

        @Override
        public void addSplitsBack(List<IntSplit> splits, int subtaskId) {}

        @Override
        public void addReader(int subtaskId) {
            context.assignSplit(new IntSplit(0, count), subtaskId);
            context.signalNoMoreSplits(subtaskId);
        }

        @Override
        public Void snapshotState(long checkpointId) {
            return null;
        }

        @Override
        public void close() {}
    }

    private static final class IntReader implements SourceReader<Integer, IntSplit> {
        private final Queue<IntSplit> splits = new ArrayDeque<>();
        private boolean noMoreSplits;

        @Override
        public void start() {}

        @Override
        public InputStatus pollNext(ReaderOutput<Integer> output) {
            final IntSplit split = splits.poll();
            if (split == null) {
                return noMoreSplits ? InputStatus.END_OF_INPUT : InputStatus.NOTHING_AVAILABLE;
            }
            for (int v = split.from; v < split.to; v++) {
                if (v % 2 == 0) {
                    output.collect(v, v);
                } else {
                    output.collect(ERROR, "odd:" + v, v);
                }
            }
            return noMoreSplits && splits.isEmpty()
                    ? InputStatus.END_OF_INPUT
                    : InputStatus.MORE_AVAILABLE;
        }

        @Override
        public List<IntSplit> snapshotState(long checkpointId) {
            return new ArrayList<>(splits);
        }

        @Override
        public CompletableFuture<Void> isAvailable() {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void addSplits(List<IntSplit> newSplits) {
            splits.addAll(newSplits);
        }

        @Override
        public void notifyNoMoreSplits() {
            noMoreSplits = true;
        }

        @Override
        public void close() {}
    }

    private static final class IntSplitSerializer implements SimpleVersionedSerializer<IntSplit> {
        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public byte[] serialize(IntSplit split) {
            return java.nio.ByteBuffer.allocate(8).putInt(split.from).putInt(split.to).array();
        }

        @Override
        public IntSplit deserialize(int version, byte[] serialized) {
            final java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(serialized);
            return new IntSplit(buf.getInt(), buf.getInt());
        }
    }

    private static final class VoidSerializer implements SimpleVersionedSerializer<Void> {
        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public byte[] serialize(Void obj) throws IOException {
            return new byte[0];
        }

        @Override
        public Void deserialize(int version, byte[] serialized) {
            return null;
        }
    }
}
