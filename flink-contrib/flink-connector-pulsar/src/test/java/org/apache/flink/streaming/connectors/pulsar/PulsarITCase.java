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

package org.apache.flink.streaming.connectors.pulsar;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.connectors.pulsar.source.MessageDeserializer;
import org.apache.flink.connectors.pulsar.source.PulsarSource;
import org.apache.flink.connectors.pulsar.source.PulsarSourceOptions;
import org.apache.flink.connectors.pulsar.source.StopCondition;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.function.ThrowingConsumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PulsarContainer;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PulsarITCase {
	private static final Logger LOG = LoggerFactory.getLogger(PulsarITCase.class);
	private static final String TOPIC_BASE = "test-topic";

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

//	@Rule
	public final Timeout timeout = Timeout.builder()
		.withTimeout(300, TimeUnit.SECONDS)
		.build();

	@Rule
	public PulsarContainer pulsarContainer = new PulsarContainer("2.6.0");

	private String topic;
	private PulsarAdmin pulsarAdmin;

	@Before
	public void setupTopic() throws PulsarClientException {
		topic = TOPIC_BASE;

		// Create a Pulsar client instance. A single instance can be shared across many
		// producers and consumer within the same application
		pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl(pulsarContainer.getHttpServiceUrl()).build();

	}

	private <E extends Throwable> void withProducer(ThrowingConsumer<Producer<byte[]>, E> producerConsumer) throws E, PulsarClientException {
		try (PulsarClient client = PulsarClient.builder()
			.serviceUrl(pulsarContainer.getPulsarBrokerUrl())
			.build();
			Producer<byte[]> producer = client.newProducer()
				// Set the topic
				.topic(topic)
				// Enable compression
				.compressionType(CompressionType.LZ4)
				.create()) {

			producerConsumer.accept(producer);
		}
	}

	@Test
	public void shouldPerformUnalignedCheckpointOnNonParallelLocalChannel() throws Exception {
		pulsarAdmin.topics().createNonPartitionedTopic(topic);
		SimpleStringSchema stringSchema = new SimpleStringSchema();
		withProducer(producer -> {
			producer.send(stringSchema.serialize("first"));
			producer.send(stringSchema.serialize("second"));
			producer.send(stringSchema.serialize("third"));
		});

		StreamExecutionEnvironment env = createEnv(1, 1);
		createDAG(env);
		env.execute();
	}

	@Nonnull
	private LocalStreamEnvironment createEnv(int parallelism, int slotsPerTaskManager) throws IOException {
		Configuration conf = new Configuration();
		conf.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, slotsPerTaskManager);
		conf.setInteger(ConfigConstants.LOCAL_NUMBER_TASK_MANAGER, parallelism / slotsPerTaskManager + 1);

		conf.setString(CheckpointingOptions.STATE_BACKEND, "filesystem");
		conf.setString(CheckpointingOptions.CHECKPOINTS_DIRECTORY, temp.newFolder().toURI().toString());

		final LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(parallelism, conf);
		env.enableCheckpointing(100);
		env.setRestartStrategy(RestartStrategies.fixedDelayRestart(1, Time.milliseconds(100)));
		return env;
	}

	private void createDAG(StreamExecutionEnvironment env) {
		PulsarSource<String> source = PulsarSource.builder()
			.setTopics(topic)
			.setDeserializer(MessageDeserializer.valueOnly(new SimpleStringSchema()))
			.stopAt(StopCondition.stopAfterLast())
			.configure(conf -> conf.set(PulsarSourceOptions.ADMIN_URL, pulsarContainer.getHttpServiceUrl()))
			.configurePulsarClient(conf -> conf.setServiceUrl(pulsarContainer.getPulsarBrokerUrl()))
			.build();
		env.fromSource(source, WatermarkStrategy.noWatermarks(), "Source")
			.addSink(new VerifyingSink());
	}


	static void info(RuntimeContext runtimeContext, String description, Object[] args) {
		LOG.info(description + " @ {} subtask ({} attempt)",
			ArrayUtils.addAll(args, new Object[]{runtimeContext.getIndexOfThisSubtask(), runtimeContext.getAttemptNumber()}));
	}

	private static class VerifyingSink extends RichSinkFunction<String> {
		private void info(String description, Object... args) {
			PulsarITCase.info(getRuntimeContext(), description, args);
		}

		@Override
		public void invoke(String value, Context context) throws Exception {
			System.out.println(value);
		}
	}
}
