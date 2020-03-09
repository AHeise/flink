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
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.io.network.BufferPersister;
import org.apache.flink.runtime.io.network.api.CancelCheckpointMarker;
import org.apache.flink.runtime.io.network.api.CheckpointBarrier;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.util.Arrays;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * {@link CheckpointBarrierUnaligner} is used for triggering checkpoint while reading the first barrier
 * and keeping track of the number of received barriers and consumed barriers.
 */
@Internal
public class CheckpointBarrierUnaligner extends CheckpointBarrierHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CheckpointBarrierUnaligner.class);

	private final String taskName;

	/**
	 * Tag the state of which input channel has read the barrier. If one channel has read the barrier by task,
	 * the respective in-flight input buffers should be empty when triggering unaligned checkpoint .
	 */
	private final boolean[] barrierConsumedChannels;

	/**
	 * The number of input channels which has read the barrier by task.
	 */
	private int numBarriersConsumed;

	/**
	 * The checkpoint id to guarantee that we would trigger only one checkpoint when reading the same barrier from
	 * different channels.
	 */
	private long currentCheckpointId = -1L;

	private final BufferPersister inputPersister;

	CheckpointBarrierUnaligner(
			int totalNumberOfInputChannels,
			String taskName,
			BufferPersister inputPersister,
			@Nullable AbstractInvokable toNotifyOnCheckpoint) {
		super(toNotifyOnCheckpoint);

		this.taskName = taskName;
		this.inputPersister = checkNotNull(inputPersister);
		this.barrierConsumedChannels = new boolean[totalNumberOfInputChannels];
		Arrays.fill(barrierConsumedChannels, true);
	}

	@Override
	public void releaseBlocksAndResetBarriers() {
	}

	/**
	 * For unaligned checkpoint, it never blocks processing from the task aspect.
	 *
	 * <p>For PoC, we do not consider the possibility that the unaligned checkpoint would
	 * not perform due to the max configured unaligned checkpoint size.
	 */
	@Override
	public boolean isBlocked(int channelIndex) {
		return false;
	}

	@Override
	public boolean isBarrierConsumed(int channelIndex) {
		return barrierConsumedChannels[channelIndex];
	}

	/**
	 * We still need to trigger checkpoint while reading the first barrier from one channel, because this might happen
	 * earlier than the previous async trigger via mailbox by netty thread. And the {@link AbstractInvokable} has the
	 * deduplication logic to guarantee trigger checkpoint only once finally.
	 *
	 * <p>Note this is also suitable for the trigger case of local input channel.
	 */
	@Override
	public synchronized boolean processBarrier(CheckpointBarrier receivedBarrier, int channelIndex,
		long bufferedBytes) throws Exception {
		final long barrierId = receivedBarrier.getId();

		if (checkNewCheckpoint(barrierId)) {
			triggerCheckpoint(receivedBarrier);
			// TODO: at this point we need to make sure that no data is read until the checkpoint has actually been
			//  triggered
		}

		handleBarrier(barrierId, channelIndex);
		return false;
	}

	@Override
	public boolean processCancellationBarrier(CancelCheckpointMarker cancelBarrier) {
		return false;
	}

	@Override
	public boolean processEndOfPartition() {
		return false;
	}

	@Override
	public long getLatestCheckpointId() {
		return currentCheckpointId;
	}

	@Override
	public long getAlignmentDurationNanos() {
		return 0;
	}

	@Override
	public String toString() {
		return String.format("%s: last checkpoint: %d", taskName, currentCheckpointId);
	}

	@Override
	public void checkpointSizeLimitExceeded(long maxBufferedBytes) {
	}

	@Override
	public synchronized void notifyBarrierReceived(CheckpointBarrier barrier, int channelIndex) {
		long barrierId = barrier.getId();

		if (checkNewCheckpoint(barrierId)) {
			triggerCheckpoint(barrier);
		}

		handleBarrier(barrierId, channelIndex);
	}

	@Override
	public synchronized void notifyBufferReceived(Buffer buffer, int channelIndex) {
		// we do not guarantee that the spilled buffers in one channel are close with each other for PoC.
		// Considering failure recovery future, we should guarantee it.
		if (!barrierConsumedChannels[channelIndex]) {
			inputPersister.addBuffer(buffer, channelIndex);
		} else {
			buffer.recycleBuffer();
		}
	}

	/**
	 * Note that we make the assumption that there is only one checkpoint under going at a time. That means one channel
	 * would not receive a bigger checkpoint id than other channels during alignment.
	 */
	private void handleBarrier(long barrierId, int channelIndex) {
		if (barrierId == currentCheckpointId && !barrierConsumedChannels[channelIndex]) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("{}: Received barrier from channel {} @ {}.", taskName, channelIndex, barrierId);
			}

			barrierConsumedChannels[channelIndex] = true;

			if (++numBarriersConsumed == barrierConsumedChannels.length) {
				inputPersister.finish(barrierId);
			}
		}
	}

	private boolean checkNewCheckpoint(long barrierId) {
		final boolean newCheckpoint = currentCheckpointId < barrierId;
		if (newCheckpoint) {
			inputPersister.notifyCheckpointStarted(barrierId);
			currentCheckpointId = barrierId;
			numBarriersConsumed = 0;
			releaseConsumedAndResetBarriers();
		}
		return newCheckpoint;
	}

	private void releaseConsumedAndResetBarriers() {
		Arrays.fill(barrierConsumedChannels, false);
		numBarriersConsumed = 0;
	}

	private void triggerCheckpoint(CheckpointBarrier barrier) {
		if (toNotifyOnCheckpoint != null) {
			toNotifyOnCheckpoint.triggerCheckpointAsync(
				new CheckpointMetaData(barrier.getId(), barrier.getTimestamp()),
				CheckpointOptions.forCheckpointWithDefaultLocation(),
				false);
		}
	}
}
