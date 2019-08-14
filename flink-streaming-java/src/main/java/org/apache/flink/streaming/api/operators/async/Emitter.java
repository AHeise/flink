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

package org.apache.flink.streaming.api.operators.async;

import org.apache.flink.annotation.Internal;
import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.api.operators.TimestampedCollector;
import org.apache.flink.streaming.api.operators.async.queue.AsyncCollectionResult;
import org.apache.flink.streaming.api.operators.async.queue.AsyncResult;
import org.apache.flink.streaming.api.operators.async.queue.AsyncWatermarkResult;
import org.apache.flink.streaming.api.operators.async.queue.StreamElementQueue;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

/**
 * Runnable responsible for consuming elements from the given queue and outputting them to the
 * given output/timestampedCollector.
 *
 * @param <OUT> Type of the output elements
 */
@Internal
public class Emitter<OUT> {

	private static final Logger LOG = LoggerFactory.getLogger(Emitter.class);

	/** Output for the watermark elements. */
	private final Output<StreamRecord<OUT>> output;

	/** Queue to consume the async results from. */
	private final StreamElementQueue streamElementQueue;

	private final OperatorActions operatorActions;

	/** Output for stream records. */
	private final TimestampedCollector<OUT> timestampedCollector;

	public Emitter(
			final Output<StreamRecord<OUT>> output,
			final StreamElementQueue streamElementQueue,
			final OperatorActions operatorActions) {

		this.output = Preconditions.checkNotNull(output, "output");
		this.streamElementQueue = Preconditions.checkNotNull(streamElementQueue, "streamElementQueue");
		this.operatorActions = Preconditions.checkNotNull(operatorActions, "operatorActions");

		this.timestampedCollector = new TimestampedCollector<>(this.output);
	}

	public boolean tryRun() throws InterruptedException {
		Optional<AsyncResult> streamElementEntry = streamElementQueue.tryPoll();

		if (!streamElementEntry.isPresent()) {
			return false;
		}

		output(streamElementEntry.get());
		return true;
	}

	private void output(AsyncResult asyncResult) {
		if (asyncResult.isWatermark()) {
				AsyncWatermarkResult asyncWatermarkResult = asyncResult.asWatermark();

				LOG.debug("Output async watermark.");
				output.emitWatermark(asyncWatermarkResult.getWatermark());
		} else {
			AsyncCollectionResult<OUT> streamRecordResult = asyncResult.asResultCollection();

			if (streamRecordResult.hasTimestamp()) {
				timestampedCollector.setAbsoluteTimestamp(streamRecordResult.getTimestamp());
			} else {
				timestampedCollector.eraseTimestamp();
			}

			LOG.debug("Output async stream element collection result.");

			try {
				Collection<OUT> resultCollection = streamRecordResult.get();

				if (resultCollection != null) {
					for (OUT result : resultCollection) {
						timestampedCollector.collect(result);
					}
				}
			} catch (Exception e) {
				operatorActions.failOperator(
						new Exception("An async function call terminated with an exception. " +
								"Failing the AsyncWaitOperator.", e));
			}
		}
	}
}
