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

package org.apache.flink.runtime.io.network;

import org.apache.flink.annotation.Internal;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.RecoverableFsDataOutputStream;
import org.apache.flink.core.fs.RecoverableWriter;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.metrics.Counter;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.NetworkBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link BufferPersisterImpl} takes the buffers and events from a data stream and persists them asynchronously
 * using {@link RecoverableFsDataOutputStream}.
 */
@Internal
public class BufferPersisterImpl implements BufferPersister {
	private static final Logger LOG = LoggerFactory.getLogger(BufferPersisterImpl.class);

	private final Writer writer;

	public BufferPersisterImpl(Path path, Counter writtenBytes, Counter persistedBytes) throws IOException {
		writer = new Writer(FileSystem.get(path.toUri()).createRecoverableWriter(), path, writtenBytes, persistedBytes);
		writer.start();
	}

	@Override
	public void addBuffer(Buffer buffer, int channelIndex) {
		writer.add(buffer);
	}

	@Override
	public void addBuffers(Collection<Buffer> buffers, int channelIndex) {
		writer.add(buffers);
	}

	public void notifyCheckpointStarted(long checkpointId) {
		writer.notifyCheckpointStarted(checkpointId);
	}

	@Override
	public void notifyCheckpointComplete(long checkpointId) {
		writer.notifyCheckpointComplete(checkpointId);
	}

	@Override
	public void finish(long checkpointId) {
		writer.finish(checkpointId);
	}

	@Override
	public CompletableFuture<?> getCompleteFuture(long checkpointId) {
		return writer.getCompleteFuture(checkpointId);
	}

	@Override
	public void close() throws IOException, InterruptedException {
		writer.close();
	}

	private static class Writer extends Thread implements AutoCloseable {
		private volatile boolean running = true;

		private final Queue<Buffer> handover = new ArrayDeque<>();
		private final RecoverableWriter recoverableWriter;
		private final Path recoverableWriterBasePath;

		@Nullable
		private Throwable asyncException;
		private int partId;
		private final Map<Long, PersistentMarkingBuffer> markingBuffers = new HashMap<>();
		private RecoverableFsDataOutputStream currentOutputStream;

		private byte[] readBuffer = new byte[0];

		private final FileSystem fs;
		private final Counter writtenBytes;
		private final Counter persistedBytes;

		public Writer(
			RecoverableWriter recoverableWriter,
			Path recoverableWriterBasePath,
			Counter writtenBytes,
			Counter persistedBytes) throws IOException {
			this.recoverableWriter = recoverableWriter;
			this.recoverableWriterBasePath = recoverableWriterBasePath;
			fs = FileSystem.get(recoverableWriterBasePath.toUri());
			this.writtenBytes = writtenBytes;
			this.persistedBytes = persistedBytes;
		}

		public synchronized void add(Buffer buffer) {
			checkErroneousUnsafe();

			boolean wasEmpty = handover.isEmpty();
			handover.add(buffer);
			if (wasEmpty) {
				notify();
			}
		}

		public synchronized void add(Collection<Buffer> buffers) {
			checkErroneousUnsafe();

			boolean wasEmpty = handover.isEmpty();
			handover.addAll(buffers);
			if (wasEmpty) {
				notify();
			}
		}

		public synchronized void notifyCheckpointStarted(long checkpointId) {
			markingBuffers.put(checkpointId, new PersistentMarkingBuffer(checkpointId));
		}

		public synchronized void notifyCheckpointComplete(long checkpointId) {
			markingBuffers.remove(checkpointId);
		}

		public synchronized void finish(long checkpointId) {
			checkErroneousUnsafe();

			LOG.debug("Finishing {} @ {}", checkpointId, recoverableWriterBasePath);
			add(getPersistentMarkingBuffer(checkpointId));
		}

		@Override
		public void run() {
			try {
				openNewOutputStream();

				while (running) {
					final Optional<Buffer> optionalBuffer = get();
					if (optionalBuffer.isPresent()) {
						write(optionalBuffer.get());
					}
				}
			} catch (Throwable t) {
				synchronized (this) {
					if (running) {
						asyncException = t;
					}
					for (final PersistentMarkingBuffer markingBuffer : markingBuffers.values()) {
						markingBuffer.getPersistFuture().completeExceptionally(t);
					}
				}
				LOG.error("unhandled exception in the Writer", t);
			}
		}

		private void write(Buffer buffer) throws IOException {
			try {
				int offset = buffer.getMemorySegmentOffset();
				MemorySegment segment = buffer.getMemorySegment();
				int numBytes = buffer.getSize();

				//TODO we should support to write nio ByteBuffer directly instead for avoiding extra copy and memory overhead
				if (readBuffer.length < numBytes) {
					readBuffer = new byte[numBytes];
				}
				segment.get(offset, readBuffer, 0, numBytes);
				currentOutputStream.write(readBuffer, 0, numBytes);
				writtenBytes.inc(numBytes);
			} finally {
				buffer.recycleBuffer();
			}
		}

		private synchronized Optional<Buffer> get() throws InterruptedException, IOException {
			while (handover.isEmpty()) {
				wait();
			}

			Buffer buffer = handover.poll();
			if (buffer instanceof PersistentMarkingBuffer) {
				final long pos = currentOutputStream.getPos();
				currentOutputStream.closeForCommit().commit();
				persistedBytes.inc(pos);

				final Path previousPart = assemblePartFilePath(partId - 2);
				// cleanup old part, this could be done asynchronously
				if (fs.exists(previousPart)) {
					fs.delete(previousPart, true);
				}

				openNewOutputStream();
				final PersistentMarkingBuffer marker = (PersistentMarkingBuffer) buffer;
				marker.getPersistFuture().complete(null);
				LOG.debug("Finished {} @ {}", marker.getCheckpointId(), recoverableWriterBasePath);

				return Optional.empty();
			}

			return Optional.ofNullable(buffer);
		}

		synchronized CompletableFuture<?> getCompleteFuture(long checkpointId) {
			return getPersistentMarkingBuffer(checkpointId).getPersistFuture();
		}

		@Nonnull
		private PersistentMarkingBuffer getPersistentMarkingBuffer(long checkpointId) {
			return markingBuffers.computeIfAbsent(checkpointId,
						id -> {
							throw new IllegalStateException("Unknown checkpoint " + checkpointId);
						});
		}

		@Override
		public void close() throws InterruptedException, IOException {
			try {
				checkErroneousUnsafe();
				running = false;
				interrupt();
				join();
			} finally {
				currentOutputStream.close();
			}
		}

		private void openNewOutputStream() throws IOException {
			currentOutputStream = recoverableWriter.open(assemblePartFilePath(partId++));
		}

		private Path assemblePartFilePath(int partId) {
			return new Path(recoverableWriterBasePath, "part-file." + partId);
		}

		private void checkErroneousUnsafe() {
			if (asyncException != null) {
				throw new RuntimeException(asyncException);
			}
		}

		private static class PersistentMarkingBuffer extends NetworkBuffer {
			private CompletableFuture<?> persistFuture = new CompletableFuture<>();
			private final long checkpointId;

			public PersistentMarkingBuffer(long checkpointId) {
				super(
					MemorySegmentFactory.allocateUnpooledSegment(42),
					memorySegment -> {});
				this.checkpointId = checkpointId;
			}

			public CompletableFuture<?> getPersistFuture() {
				return persistFuture;
			}

			public long getCheckpointId() {
				return checkpointId;
			}
		}
	}
}
