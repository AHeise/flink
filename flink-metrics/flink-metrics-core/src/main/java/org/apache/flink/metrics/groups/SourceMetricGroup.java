/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.metrics.groups;

import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;

/** Pre-defined metrics for sources. */
public interface SourceMetricGroup extends OperatorMetricGroup {
    /** The total number of record that failed to consume, process or emit. */
    Counter getNumRecordsInErrors();

    /**
     * The time in milliseconds from the record event timestamp to the timestamp Flink fetched the
     * record.
     *
     * <p>This metric is an instantaneous value recorded for the last processed record.
     *
     * <p>This metric is provided because latency histogram could be expensive. The instantaneous
     * latency value is usually a good enough indication of the latency.
     *
     * <p>currentFetchEventTimeLag = FetchTime - EventTime
     */
    Gauge<Long> createCurrentFetchEventTimeLag(Gauge<Long> currentFetchEventTimeLagGauge);

    /**
     * The time in milliseconds from the record event timestamp to the timestamp the record is
     * emitted by the source connector.
     *
     * <p>This metric is an instantaneous value recorded for the last processed record.
     *
     * <p>This metric is provided because latency histogram could be expensive. The instantaneous
     * latency value is usually a good enough indication of the latency.
     *
     * <p>currentEmitEventTimeLag = EmitTime - EventTime, where the EmitTime is the time the record
     * leaves the source operator.
     */
    Gauge<Long> getCurrentEmitEventTimeLag();

    /**
     * The time in milliseconds that the watermark lags behind the wall clock time.
     *
     * <p>watermarkLag = CurrentTime - Watermark
     */
    Gauge<Long> getWatermarkLag();

    /**
     * The time in milliseconds that the source has not processed any record.
     *
     * <p>sourceIdleTime = CurrentTime - LastRecordProcessTime
     */
    Gauge<Long> createSourceIdleTime(Gauge<Long> sourceIdleTimeGauge);

    /**
     * The number of bytes that have not been fetched by the source. e.g. the remaining bytes in a
     * file after the file descriptor reading position.
     *
     * <p>Note that not every source reports this metric, but the metric of the same semantic should
     * be reported with this name and specification if the Source does report.
     */
    Gauge<Long> createPendingBytes(Gauge<Long> pendingBytesGauge);

    /**
     * The number of records that have not been fetched by the source. e.g. the available records
     * after the consumer offset in a Kafka partition.
     *
     * <p>Note that not every source reports this metric, but the metric of the same semantic should
     * be reported with this name and specification if the Source does report.
     */
    Gauge<Long> createPendingRecords(Gauge<Long> pendingRecordsGauge);
}
