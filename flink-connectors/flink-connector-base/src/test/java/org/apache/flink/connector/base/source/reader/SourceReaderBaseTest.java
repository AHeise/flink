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

import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.connector.source.internal.InternalReaderOutput;
import org.apache.flink.api.connector.source.mocks.MockSourceSplit;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.fetcher.SingleThreadFetcherManager;
import org.apache.flink.connector.base.source.reader.mocks.MockSourceReader;
import org.apache.flink.connector.base.source.reader.mocks.MockSplitReader;
import org.apache.flink.connector.base.source.reader.mocks.PassThroughRecordEmitter;
import org.apache.flink.connector.base.source.reader.mocks.TestingRecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.mocks.TestingSourceSplit;
import org.apache.flink.connector.base.source.reader.mocks.TestingSplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange;
import org.apache.flink.connector.base.source.reader.synchronization.FutureCompletingBlockingQueue;
import org.apache.flink.connector.testutils.source.reader.SourceReaderTestBase;
import org.apache.flink.connector.testutils.source.reader.TestingReaderContext;
import org.apache.flink.connector.testutils.source.reader.TestingReaderOutput;
import org.apache.flink.core.io.InputStatus;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.streaming.api.operators.source.CollectingDataOutput;
import org.apache.flink.streaming.api.operators.source.TimestampsAndWatermarks;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/** A unit test class for {@link SourceReaderBase}. */
public class SourceReaderBaseTest extends SourceReaderTestBase<MockSourceSplit> {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExceptionInSplitReader() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("One or more fetchers have encountered exception");
        final String errMsg = "Testing Exception";

        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>();
        // We have to handle split changes first, otherwise fetch will not be called.
        try (MockSourceReader reader =
                new MockSourceReader(
                        elementsQueue,
                        () ->
                                new SplitReader<int[], MockSourceSplit>() {
                                    @Override
                                    public RecordsWithSplitIds<int[]> fetch() {
                                        throw new RuntimeException(errMsg);
                                    }

                                    @Override
                                    public void handleSplitsChanges(
                                            SplitsChange<MockSourceSplit> splitsChanges) {}

                                    @Override
                                    public void wakeUp() {}

                                    @Override
                                    public void close() {}
                                },
                        getConfig(),
                        new TestingReaderContext())) {
            ValidatingSourceOutput output = new ValidatingSourceOutput();
            reader.addSplits(
                    Collections.singletonList(
                            getSplit(0, NUM_RECORDS_PER_SPLIT, Boundedness.CONTINUOUS_UNBOUNDED)));
            reader.notifyNoMoreSplits();
            // This is not a real infinite loop, it is supposed to throw exception after two polls.
            while (true) {
                InputStatus inputStatus = reader.pollNext(output);
                assertNotEquals(InputStatus.END_OF_INPUT, inputStatus);
                // Add a sleep to avoid tight loop.
                Thread.sleep(1);
            }
        }
    }

    @Test
    public void testRecordsWithSplitsNotRecycledWhenRecordsLeft() throws Exception {
        final TestingRecordsWithSplitIds<String> records =
                new TestingRecordsWithSplitIds<>("test-split", "value1", "value2");
        final SourceReader<?, ?> reader = createReaderAndAwaitAvailable("test-split", records);

        reader.pollNext(new TestingReaderOutput<>());

        assertFalse(records.isRecycled());
    }

    @Test
    public void testRecordsWithSplitsRecycledWhenEmpty() throws Exception {
        final TestingRecordsWithSplitIds<String> records =
                new TestingRecordsWithSplitIds<>("test-split", "value1", "value2");
        final SourceReader<?, ?> reader = createReaderAndAwaitAvailable("test-split", records);

        // poll thrice: twice to get all records, one more to trigger recycle and moving to the next
        // split
        reader.pollNext(new TestingReaderOutput<>());
        reader.pollNext(new TestingReaderOutput<>());
        reader.pollNext(new TestingReaderOutput<>());

        assertTrue(records.isRecycled());
    }

    @Test
    public void testMultipleSplitsWithDifferentFinishingMoments() throws Exception {
        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>();
        MockSplitReader mockSplitReader =
                MockSplitReader.newBuilder()
                        .setNumRecordsPerSplitPerFetch(2)
                        .setSeparatedFinishedRecord(false)
                        .setBlockingFetch(false)
                        .build();
        MockSourceReader reader =
                new MockSourceReader(
                        elementsQueue,
                        () -> mockSplitReader,
                        getConfig(),
                        new TestingReaderContext());

        reader.start();

        List<MockSourceSplit> splits =
                Arrays.asList(
                        getSplit(0, 10, Boundedness.BOUNDED), getSplit(1, 12, Boundedness.BOUNDED));
        reader.addSplits(splits);
        reader.notifyNoMoreSplits();

        while (true) {
            InputStatus status = reader.pollNext(new TestingReaderOutput<>());
            if (status == InputStatus.END_OF_INPUT) {
                break;
            }
            if (status == InputStatus.NOTHING_AVAILABLE) {
                reader.isAvailable().get();
            }
        }
    }

    @Test
    public void testMultipleSplitsWithSeparatedFinishedRecord() throws Exception {
        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>();
        MockSplitReader mockSplitReader =
                MockSplitReader.newBuilder()
                        .setNumRecordsPerSplitPerFetch(2)
                        .setSeparatedFinishedRecord(true)
                        .setBlockingFetch(false)
                        .build();
        MockSourceReader reader =
                new MockSourceReader(
                        elementsQueue,
                        () -> mockSplitReader,
                        getConfig(),
                        new TestingReaderContext());

        reader.start();

        List<MockSourceSplit> splits =
                Arrays.asList(
                        getSplit(0, 10, Boundedness.BOUNDED), getSplit(1, 10, Boundedness.BOUNDED));
        reader.addSplits(splits);
        reader.notifyNoMoreSplits();

        while (true) {
            InputStatus status = reader.pollNext(new TestingReaderOutput<>());
            if (status == InputStatus.END_OF_INPUT) {
                break;
            }
            if (status == InputStatus.NOTHING_AVAILABLE) {
                reader.isAvailable().get();
            }
        }
    }

    @Test
    public void testPollNextReturnMoreAvailableWhenAllSplitFetcherCloseWithLeftoverElementInQueue()
            throws Exception {

        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>();
        MockSplitReader mockSplitReader =
                MockSplitReader.newBuilder()
                        .setNumRecordsPerSplitPerFetch(1)
                        .setBlockingFetch(true)
                        .build();
        BlockingShutdownSplitFetcherManager<int[], MockSourceSplit> splitFetcherManager =
                new BlockingShutdownSplitFetcherManager<>(elementsQueue, () -> mockSplitReader);
        final MockSourceReader sourceReader =
                new MockSourceReader(
                        elementsQueue,
                        splitFetcherManager,
                        getConfig(),
                        new TestingReaderContext());

        // Create and add a split that only contains one record
        final MockSourceSplit split = new MockSourceSplit(0, 0, 1);
        sourceReader.addSplits(Collections.singletonList(split));
        sourceReader.notifyNoMoreSplits();

        // Add the last record to the split when the splitFetcherManager shutting down SplitFetchers
        splitFetcherManager.getInShutdownSplitFetcherFuture().thenRun(() -> split.addRecord(1));
        assertEquals(
                InputStatus.MORE_AVAILABLE, sourceReader.pollNext(new TestingReaderOutput<>()));
    }

    @Test
    public void testSplitAlignment() throws Exception {
        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>(1);
        MockSplitReader mockSplitReader =
                MockSplitReader.newBuilder()
                        .setNumRecordsPerSplitPerFetch(1)
                        .setBlockingFetch(false)
                        .build();
        MockSourceReader reader =
                new MockSourceReader(
                        elementsQueue,
                        () -> mockSplitReader,
                        getConfig(),
                        new TestingReaderContext());

        reader.start();

        List<MockSourceSplit> splits =
                Arrays.asList(
                        getSplit(0, 10, Boundedness.BOUNDED), getSplit(1, 10, Boundedness.BOUNDED));
        reader.addSplits(splits);
        reader.notifyNoMoreSplits();

        // add watermark assigner, where watermark = value
        TimestampsAndWatermarks<Integer> eventTimeLogic =
                TimestampsAndWatermarks.createProgressiveEventTimeLogic(
                        WatermarkStrategy.forGenerator(
                                context -> new EventAsTimestampWatermarkGenerator()),
                        UnregisteredMetricsGroup.createSourceReaderMetricGroup(),
                        new TestProcessingTimeService(),
                        100);
        CollectingDataOutput<Integer> output = new CollectingDataOutput<>();
        InternalReaderOutput<Integer> mainOutput = eventTimeLogic.createMainOutput(output);

        // poll twice to init both spits
        pollOnce(reader, mainOutput);
        pollOnce(reader, mainOutput);
        assertThat(output.getEvents())
                .filteredOn(e1 -> e1 instanceof StreamRecord)
                .map(e1 -> ((StreamRecord<Integer>) e1).getValue())
                .contains(0, 10);
        // now disable one split
        reader.setCurrentMaxWatermark(new Watermark(9));

        while (true) {
            if (pollOnce(reader, mainOutput)) {
                break;
            }

            // received the last element from first split, re-enable 2. split
            if (output.getEvents()
                    .contains(new StreamRecord<>(9, TimestampAssigner.NO_TIMESTAMP))) {
                reader.setCurrentMaxWatermark(new Watermark(19));
            }
        }

        // usually split records are interleaved, 0,10,1,11,...
        // because of disabled second split, we can now assert ordered after some initial elements
        // (3 from both splits + watermarks)
        assertThat(output.getEvents().subList(9, output.getEvents().size()))
                .filteredOn(e -> e instanceof StreamRecord)
                .map(e -> ((StreamRecord<Integer>) e).getValue())
                .isSorted();
    }

    private boolean pollOnce(MockSourceReader reader, InternalReaderOutput<Integer> mainOutput)
            throws Exception {
        InputStatus status = reader.pollNext(mainOutput);
        if (status == InputStatus.END_OF_INPUT) {
            return true;
        }
        if (status == InputStatus.NOTHING_AVAILABLE) {
            reader.isAvailable().get();
            return pollOnce(reader, mainOutput);
        }
        return false;
    }

    // ---------------- helper methods -----------------

    @Override
    protected MockSourceReader createReader() {
        FutureCompletingBlockingQueue<RecordsWithSplitIds<int[]>> elementsQueue =
                new FutureCompletingBlockingQueue<>();
        MockSplitReader mockSplitReader =
                MockSplitReader.newBuilder()
                        .setNumRecordsPerSplitPerFetch(2)
                        .setBlockingFetch(true)
                        .build();
        return new MockSourceReader(
                elementsQueue, () -> mockSplitReader, getConfig(), new TestingReaderContext());
    }

    @Override
    protected List<MockSourceSplit> getSplits(
            int numSplits, int numRecordsPerSplit, Boundedness boundedness) {
        List<MockSourceSplit> mockSplits = new ArrayList<>();
        for (int i = 0; i < numSplits; i++) {
            mockSplits.add(getSplit(i, numRecordsPerSplit, boundedness));
        }
        return mockSplits;
    }

    @Override
    protected MockSourceSplit getSplit(int splitId, int numRecords, Boundedness boundedness) {
        MockSourceSplit mockSplit;
        if (boundedness == Boundedness.BOUNDED) {
            mockSplit = new MockSourceSplit(splitId, 0, numRecords);
        } else {
            mockSplit = new MockSourceSplit(splitId);
        }
        for (int j = 0; j < numRecords; j++) {
            mockSplit.addRecord(splitId * 10 + j);
        }
        return mockSplit;
    }

    @Override
    protected long getNextRecordIndex(MockSourceSplit split) {
        return split.index();
    }

    private Configuration getConfig() {
        Configuration config = new Configuration();
        config.setInteger(SourceReaderOptions.ELEMENT_QUEUE_CAPACITY, 1);
        config.setLong(SourceReaderOptions.SOURCE_READER_CLOSE_TIMEOUT, 30000L);
        return config;
    }

    // ------------------------------------------------------------------------
    //  Testing Setup Helpers
    // ------------------------------------------------------------------------

    private static <E> SourceReader<E, ?> createReaderAndAwaitAvailable(
            final String splitId, final RecordsWithSplitIds<E> records) throws Exception {

        final FutureCompletingBlockingQueue<RecordsWithSplitIds<E>> elementsQueue =
                new FutureCompletingBlockingQueue<>();

        final SourceReader<E, TestingSourceSplit> reader =
                new SingleThreadMultiplexSourceReaderBase<
                        E, E, TestingSourceSplit, TestingSourceSplit>(
                        elementsQueue,
                        () -> new TestingSplitReader<>(records),
                        new PassThroughRecordEmitter<>(),
                        new Configuration(),
                        new TestingReaderContext()) {

                    @Override
                    public void notifyCheckpointComplete(long checkpointId) {}

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

        final List<TestingSourceSplit> splits =
                Collections.singletonList(new TestingSourceSplit(splitId));
        reader.addSplits(splits);

        reader.isAvailable().get();

        return reader;
    }

    // ------------------ Test helper classes -------------------
    /**
     * When maybeShutdownFinishedFetchers is invoke, BlockingShutdownSplitFetcherManager will
     * complete the inShutdownSplitFetcherFuture and ensures that all the split fetchers are
     * shutdown.
     */
    private static class BlockingShutdownSplitFetcherManager<E, SplitT extends SourceSplit>
            extends SingleThreadFetcherManager<E, SplitT> {

        private final CompletableFuture<Void> inShutdownSplitFetcherFuture;

        public BlockingShutdownSplitFetcherManager(
                FutureCompletingBlockingQueue<RecordsWithSplitIds<E>> elementsQueue,
                Supplier<SplitReader<E, SplitT>> splitReaderSupplier) {
            super(elementsQueue, splitReaderSupplier);
            this.inShutdownSplitFetcherFuture = new CompletableFuture<>();
        }

        @Override
        public boolean maybeShutdownFinishedFetchers() {
            shutdownAllSplitFetcher();
            return true;
        }

        public CompletableFuture<Void> getInShutdownSplitFetcherFuture() {
            return inShutdownSplitFetcherFuture;
        }

        private void shutdownAllSplitFetcher() {
            inShutdownSplitFetcherFuture.complete(null);
            while (!super.maybeShutdownFinishedFetchers()) {
                try {
                    // avoid tight loop
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class EventAsTimestampWatermarkGenerator implements WatermarkGenerator<Integer> {

        @Override
        public void onEvent(Integer event, long eventTimestamp, WatermarkOutput output) {
            output.emitWatermark(new Watermark(event.longValue()));
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {}
    }
}
