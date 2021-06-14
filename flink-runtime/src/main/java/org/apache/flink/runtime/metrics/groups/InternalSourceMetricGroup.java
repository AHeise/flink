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

package org.apache.flink.runtime.metrics.groups;

import org.apache.flink.annotation.Internal;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.groups.OperatorIOMetricGroup;
import org.apache.flink.metrics.groups.OperatorMetricGroup;
import org.apache.flink.metrics.groups.SourceMetricGroup;
import org.apache.flink.metrics.groups.TaskIOMetricGroup;
import org.apache.flink.runtime.metrics.MetricNames;
import org.apache.flink.runtime.metrics.TimerGauge;

/** Special {@link org.apache.flink.metrics.MetricGroup} representing an Operator. */
@Internal
public class InternalSourceMetricGroup extends ProxyMetricGroup<OperatorMetricGroup>
        implements SourceMetricGroup {

    private final Counter numRecordsInErrors;
    private final TimerGauge currentEmitEventTimeLag;
    private final TimerGauge watermarkLag;

    public InternalSourceMetricGroup(OperatorMetricGroup parentMetricGroup) {
        super(parentMetricGroup);
        numRecordsInErrors = parentMetricGroup.counter(MetricNames.NUM_RECORDS_IN_ERRORS);
        currentEmitEventTimeLag =
                parentMetricGroup.gauge(MetricNames.CURRENT_EMIT_EVENT_TIME_LAG, new TimerGauge());
        watermarkLag = parentMetricGroup.gauge(MetricNames.WATERMARK_LAG, new TimerGauge());
    }

    @Override
    public Counter getNumRecordsInErrors() {
        return numRecordsInErrors;
    }

    @Override
    public Gauge<Long> createCurrentFetchEventTimeLag(Gauge<Long> currentFetchEventTimeLagGauge) {
        return gauge(MetricNames.CURRENT_FETCH_EVENT_TIME_LAG_GAUGE, currentFetchEventTimeLagGauge);
    }

    @Override
    public Gauge<Long> getCurrentEmitEventTimeLag() {
        return currentEmitEventTimeLag;
    }

    @Override
    public Gauge<Long> getWatermarkLag() {
        return watermarkLag;
    }

    @Override
    public Gauge<Long> createSourceIdleTime(Gauge<Long> sourceIdleTimeGauge) {
        return gauge(MetricNames.SOURCE_IDLE_TIME_GAUGE, sourceIdleTimeGauge);
    }

    @Override
    public Gauge<Long> createPendingBytes(Gauge<Long> pendingBytesGauge) {
        return gauge(MetricNames.PENDING_BYTES_GAUGE, pendingBytesGauge);
    }

    @Override
    public Gauge<Long> createPendingRecords(Gauge<Long> pendingRecordsGauge) {
        return gauge(MetricNames.PENDING_RECORDS_GAUGE, pendingRecordsGauge);
    }

    @Override
    public OperatorIOMetricGroup getIOMetricGroup() {
        return parentMetricGroup.getIOMetricGroup();
    }

    @Override
    public TaskIOMetricGroup getTaskIOMetricGroup() {
        return parentMetricGroup.getTaskIOMetricGroup();
    }
}
