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

package org.apache.flink.streaming.runtime.operators.sink;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamEdge;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.streaming.api.graph.StreamNode;
import org.apache.flink.util.ErrorOutputTag;
import org.apache.flink.util.OutputTag;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link DataStreamSink#getSideOutput} on a Sink V2 sink produces a side-output edge
 * off the sink writer node.
 */
class SinkSideOutputTranslationTest {

    private static final OutputTag<String> ERROR = new ErrorOutputTag<>("dlq", Types.STRING);

    @Test
    void getSideOutputOnSinkProducesTaggedEdgeOffWriterNode() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        final DataStreamSource<Integer> source = env.fromData(1, 2, 3);
        final DataStreamSink<Integer> sink = source.sinkTo(new NoOpSink());
        final SideOutputDataStream<String> errors = sink.getSideOutput(ERROR);
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
        // The side-output edge originates from the sink writer operator.
        assertThat(streamGraph.getStreamNode(sideEdge.getSourceId()).getOperatorName())
                .contains("Writer");
    }

    private static final class NoOpSink implements Sink<Integer> {
        @Override
        public SinkWriter<Integer> createWriter(WriterInitContext context) {
            return new NoOpWriter();
        }
    }

    private static final class NoOpWriter implements SinkWriter<Integer> {
        @Override
        public void write(Integer element, Context context) {}

        @Override
        public void flush(boolean endOfInput) {}

        @Override
        public void close() {}
    }
}
