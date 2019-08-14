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

package org.apache.flink.streaming.api.operators.async.queue;

import org.apache.flink.streaming.api.operators.async.OperatorActions;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

import static org.apache.flink.streaming.api.operators.async.queue.StreamElementQueueTest.StreamElementQueueType.OrderedStreamElementQueueType;
import static org.apache.flink.streaming.api.operators.async.queue.StreamElementQueueTest.StreamElementQueueType.UnorderedStreamElementQueueType;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for the basic functionality of {@link StreamElementQueue}. The basic operations consist
 * of putting and polling elements from the queue.
 */
@RunWith(Parameterized.class)
public class StreamElementQueueTest extends TestLogger {
	@Rule
	public ExecutorServiceRule executor = new ExecutorServiceRule(() -> Executors.newFixedThreadPool(3));

	enum StreamElementQueueType {
		OrderedStreamElementQueueType,
		UnorderedStreamElementQueueType
	}

	@Parameterized.Parameters
	public static Collection<StreamElementQueueType> streamElementQueueTypes() {
		return Arrays.asList(OrderedStreamElementQueueType, UnorderedStreamElementQueueType);
	}

	private final StreamElementQueueType streamElementQueueType;

	public StreamElementQueueTest(StreamElementQueueType streamElementQueueType) {
		this.streamElementQueueType = Preconditions.checkNotNull(streamElementQueueType);
	}

	public StreamElementQueue createStreamElementQueue(int capacity, OperatorActions operatorActions) {
		switch (streamElementQueueType) {
			case OrderedStreamElementQueueType:
				return new OrderedStreamElementQueue(capacity, executor, operatorActions);
			case UnorderedStreamElementQueueType:
				return new UnorderedStreamElementQueue(capacity, executor, operatorActions);
			default:
				throw new IllegalStateException("Unknown stream element queue type: " + streamElementQueueType);
		}
	}

	@Test
	public void testPut() throws InterruptedException {
		OperatorActions operatorActions = mock(OperatorActions.class);
		StreamElementQueue queue = createStreamElementQueue(2, operatorActions);

		final Watermark watermark = new Watermark(0L);
		final StreamRecord<Integer> streamRecord = new StreamRecord<>(42, 1L);
		final Watermark nextWatermark = new Watermark(2L);

		final WatermarkQueueEntry watermarkQueueEntry = new WatermarkQueueEntry(watermark);
		final StreamRecordQueueEntry<Integer> streamRecordQueueEntry = new StreamRecordQueueEntry<>(streamRecord);

		queue.put(watermarkQueueEntry);
		queue.put(streamRecordQueueEntry);

		Assert.assertEquals(2, queue.size());

		Assert.assertFalse(queue.tryPut(new WatermarkQueueEntry(nextWatermark)));

		Collection<StreamElementQueueEntry<?>> actualValues = queue.values();

		List<StreamElementQueueEntry<?>> expectedValues = Arrays.asList(watermarkQueueEntry, streamRecordQueueEntry);

		Assert.assertEquals(expectedValues, actualValues);

		verify(operatorActions, never()).failOperator(any(Exception.class));
	}

	@Test
	public void testPoll() throws InterruptedException {
		OperatorActions operatorActions = mock(OperatorActions.class);
		StreamElementQueue queue = createStreamElementQueue(2, operatorActions);

		WatermarkQueueEntry watermarkQueueEntry = new WatermarkQueueEntry(new Watermark(0L));
		StreamRecordQueueEntry<Integer> streamRecordQueueEntry = new StreamRecordQueueEntry<>(new StreamRecord<>(42, 1L));

		queue.put(watermarkQueueEntry);
		queue.put(streamRecordQueueEntry);

		Assert.assertEquals(2, queue.size());

		Assert.assertEquals(watermarkQueueEntry, pollBlocking(queue));
		Assert.assertEquals(1, queue.size());

		streamRecordQueueEntry.complete(Collections.<Integer>emptyList());

		Assert.assertEquals(streamRecordQueueEntry, pollBlocking(queue));

		Assert.assertEquals(0, queue.size());
		Assert.assertTrue(queue.isEmpty());

		verify(operatorActions, never()).failOperator(any(Exception.class));
	}

	/**
	 * Tests that a put operation blocks if the queue is full.
	 */
	@Test
	public void testBlockingPut() throws Exception {
		OperatorActions operatorActions = mock(OperatorActions.class);
		final StreamElementQueue queue = createStreamElementQueue(1, operatorActions);

		StreamRecordQueueEntry<Integer> streamRecordQueueEntry = new StreamRecordQueueEntry<>(new StreamRecord<>(42, 0L));
		final StreamRecordQueueEntry<Integer> streamRecordQueueEntry2 = new StreamRecordQueueEntry<>(new StreamRecord<>(43, 1L));

		queue.put(streamRecordQueueEntry);

		Assert.assertEquals(1, queue.size());

		CompletableFuture<Void> putOperation = CompletableFuture.runAsync(
			() -> {
				try {
					queue.put(streamRecordQueueEntry2);
				} catch (InterruptedException e) {
					throw new CompletionException(e);
				}
			},
			executor);

		// give the future a chance to complete
		Thread.sleep(10L);

		// but it shouldn't ;-)
		Assert.assertFalse(putOperation.isDone());

		streamRecordQueueEntry.complete(Collections.<Integer>emptyList());

		// polling the completed head element frees the queue again
		Assert.assertEquals(streamRecordQueueEntry, pollBlocking(queue));

		// now the put operation should complete
		putOperation.get();

		verify(operatorActions, never()).failOperator(any(Exception.class));
	}

	private AsyncResult pollBlocking(StreamElementQueue queue) throws InterruptedException {
		Optional<AsyncResult> result;
		do {
			result = queue.tryPoll();
		} while (!result.isPresent());
		return result.get();
	}
}
