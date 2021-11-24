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

package org.apache.flink.connector.base.source.reader.fetcher;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.base.source.reader.splitreader.AlignmentAwareSplitReader;

import java.io.IOException;
import java.util.Collection;

/**
 * Changes the paused splits of a {@link AlignmentAwareSplitReader}. The task is used by default in
 * {@link SplitFetcherManager} and assumes that a {@link SplitFetcher} has multiple splits.
 *
 * @param <SplitT> the type of the split
 */
@Internal
class AlignmentTask<SplitT extends SourceSplit> implements SplitFetcherTask {

    private final AlignmentAwareSplitReader<?, SplitT> splitReader;
    private final Collection<SplitT> splitsToResume;
    private final Collection<SplitT> splitsToPause;

    AlignmentTask(
            AlignmentAwareSplitReader<?, SplitT> splitReader,
            Collection<SplitT> splitsToResume,
            Collection<SplitT> splitsToPause) {
        this.splitReader = splitReader;
        this.splitsToResume = splitsToResume;
        this.splitsToPause = splitsToPause;
    }

    @Override
    public boolean run() throws IOException {
        splitReader.alignSplits(splitsToPause, splitsToResume);
        return true;
    }

    @Override
    public void wakeUp() {}

    @Override
    public String toString() {
        return "AlignmentTask{"
                + "splitsToResume="
                + splitsToResume
                + ", splitsToPause="
                + splitsToPause
                + '}';
    }
}
