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

package org.apache.flink.streaming.runtime.io;

import org.apache.flink.annotation.Internal;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.runtime.checkpoint.CheckpointException;
import org.apache.flink.runtime.checkpoint.CheckpointFailureReason;
import org.apache.flink.runtime.checkpoint.channel.ChannelStateWriter;
import org.apache.flink.runtime.checkpoint.channel.InputChannelInfo;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.io.network.api.CancelCheckpointMarker;
import org.apache.flink.runtime.io.network.api.CheckpointBarrier;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.partition.consumer.InputGate;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.streaming.runtime.tasks.SubtaskCheckpointCoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.flink.runtime.checkpoint.CheckpointFailureReason.CHECKPOINT_DECLINED_SUBSUMED;
import static org.apache.flink.util.CloseableIterator.ofElement;

/**
 * {@link CheckpointBarrierUnaligner} is used for triggering checkpoint while reading the first barrier
 * and keeping track of the number of received barriers and consumed barriers.
 */
@Internal
@NotThreadSafe
public class CheckpointBarrierUnaligner extends CheckpointBarrierHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CheckpointBarrierUnaligner.class);

	private final String taskName;

	/**
	 * Tag the state of which input channel has not received the barrier, such that newly arriving buffers need
	 * to be written in the unaligned checkpoint.
	 */
	private final Map<InputChannelInfo, Boolean> storeNewBuffers;

	private int numBarriersReceived;

	/** A future indicating that all barriers of the a given checkpoint have been read. */
	private CompletableFuture<Void> allBarriersReceivedFuture = FutureUtils.completedVoidFuture();

	/**
	 * The checkpoint id to guarantee that we would trigger only one checkpoint when reading the same barrier from
	 * different channels.
	 */
	private long currentCheckpointId = -1L;

	private int numOpenChannels;

	private final SubtaskCheckpointCoordinator checkpointCoordinator;

	private final InputGate[] inputGates;

	CheckpointBarrierUnaligner(
			SubtaskCheckpointCoordinator checkpointCoordinator,
			String taskName,
			AbstractInvokable toNotifyOnCheckpoint,
			InputGate... inputGates) {
		super(toNotifyOnCheckpoint);

		this.taskName = taskName;
		this.inputGates = inputGates;
		storeNewBuffers = Arrays.stream(inputGates)
			.flatMap(gate -> gate.getChannelInfos().stream())
			.collect(Collectors.toMap(Function.identity(), info -> false));
		numOpenChannels = storeNewBuffers.size();
		this.checkpointCoordinator = checkpointCoordinator;
	}

	@Override
	public void processBarrier(CheckpointBarrier barrier, InputChannelInfo channelInfo) throws IOException {
		long barrierId = barrier.getId();
		if (currentCheckpointId > barrierId || (currentCheckpointId == barrierId && !isCheckpointPending())) {
			// ignore old and cancelled barriers
			return;
		}
		if (currentCheckpointId < barrierId) {
			handleNewCheckpoint(barrier);
			notifyCheckpoint(barrier, 0);
		}
		if (currentCheckpointId == barrierId) {
			if (storeNewBuffers.put(channelInfo, false)) {
				LOG.debug("{}: Received barrier from channel {} @ {}.", taskName, channelInfo, barrierId);

				inputGates[channelInfo.getGateIdx()].getChannel(channelInfo.getInputChannelIdx())
					.spillInflightBuffers(barrierId, checkpointCoordinator.getChannelStateWriter());

				if (++numBarriersReceived == numOpenChannels) {
					allBarriersReceivedFuture.complete(null);
				}
			}
		}
	}

	@Override
	public void abortPendingCheckpoint(long checkpointId, CheckpointException exception) throws IOException {
		tryAbortPendingCheckpoint(checkpointId, exception);

		if (checkpointId > currentCheckpointId) {
			resetPendingCheckpoint();
		}
	}

	@Override
	public void processCancellationBarrier(CancelCheckpointMarker cancelBarrier) throws IOException {
		final long cancelledId = cancelBarrier.getCheckpointId();
		boolean shouldAbort = setCancelledCheckpointId(cancelledId);
		if (shouldAbort) {
			notifyAbort(
				cancelledId,
				new CheckpointException(CheckpointFailureReason.CHECKPOINT_DECLINED_ON_CANCELLATION_BARRIER));
		}

		if (cancelledId >= currentCheckpointId) {
			resetPendingCheckpoint();
			currentCheckpointId = cancelledId;
		}
	}

	@Override
	public void processEndOfPartition() throws IOException {
		numOpenChannels--;

		resetPendingCheckpoint();
		notifyAbort(
			currentCheckpointId,
			new CheckpointException(CheckpointFailureReason.CHECKPOINT_DECLINED_INPUT_END_OF_STREAM));
	}

	private void resetPendingCheckpoint() {
		LOG.warn("{}: Received barrier or EndOfPartition(-1) before completing current checkpoint {}. " +
				"Skipping current checkpoint.",
			taskName,
			currentCheckpointId);

		storeNewBuffers.entrySet().forEach(storeNewBuffer -> storeNewBuffer.setValue(false));
		numBarriersReceived = 0;
	}

	@Override
	public long getLatestCheckpointId() {
		return currentCheckpointId;
	}

	@Override
	public String toString() {
		return String.format("%s: last checkpoint: %d", taskName, currentCheckpointId);
	}

	@Override
	public void close() throws IOException {
		super.close();
		allBarriersReceivedFuture.cancel(false);
	}

	@Override
	protected boolean isCheckpointPending() {
		return numBarriersReceived > 0;
	}

	@Override
	public void processBuffer(Buffer buffer, InputChannelInfo channelInfo) {
		if (storeNewBuffers.get(channelInfo)) {
			checkpointCoordinator.getChannelStateWriter().addInputData(
				currentCheckpointId,
				channelInfo,
				ChannelStateWriter.SEQUENCE_NUMBER_UNKNOWN,
				ofElement(buffer.retainBuffer(), Buffer::recycleBuffer));
		}
	}

	private void handleNewCheckpoint(CheckpointBarrier barrier) throws IOException {
		long barrierId = barrier.getId();
		if (!allBarriersReceivedFuture.isDone()) {
			CheckpointException exception = new CheckpointException("Barrier id: " + barrierId,
				CHECKPOINT_DECLINED_SUBSUMED);
			if (isCheckpointPending()) {
				// we did not complete the current checkpoint, another started before
				LOG.warn("{}: Received checkpoint barrier for checkpoint {} before completing current checkpoint {}. " +
						"Skipping current checkpoint.",
					taskName,
					barrierId,
					currentCheckpointId);

				// let the task know we are not completing this
				final long currentCheckpointId = this.currentCheckpointId;
				notifyAbort(currentCheckpointId, exception);
			}
			allBarriersReceivedFuture.completeExceptionally(exception);
		}

		markCheckpointStart(barrier.getTimestamp());
		currentCheckpointId = barrierId;
		storeNewBuffers.entrySet().forEach(storeNewBuffer -> storeNewBuffer.setValue(true));
		numBarriersReceived = 0;
		allBarriersReceivedFuture = new CompletableFuture<>();
		checkpointCoordinator.initCheckpoint(barrierId, barrier.getCheckpointOptions());
	}

	public CompletableFuture<Void> getAllBarriersReceivedFuture(long checkpointId) {
		if (checkpointId < currentCheckpointId) {
			return FutureUtils.completedVoidFuture();
		}
		if (checkpointId > currentCheckpointId) {
			throw new IllegalStateException("Checkpoint " + checkpointId + " has not been started at all");
		}
		return allBarriersReceivedFuture;
	}

	boolean setCancelledCheckpointId(long cancelledId) {
		if (currentCheckpointId > cancelledId || (currentCheckpointId == cancelledId && numBarriersReceived == 0)) {
			return false;
		}

		resetPendingCheckpoint();
		currentCheckpointId = cancelledId;
		return true;
	}

	void tryAbortPendingCheckpoint(long checkpointId, CheckpointException exception) throws IOException {
		if (checkpointId > currentCheckpointId) {
			resetPendingCheckpoint();
			notifyAbort(currentCheckpointId, exception);
		}
	}

	@VisibleForTesting
	int getNumOpenChannels() {
		return numOpenChannels;
	}
}
