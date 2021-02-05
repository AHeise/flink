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

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.rabbitmq2.ConsistencyMode;
import org.apache.flink.connector.rabbitmq2.source.RabbitMQSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");

        //    	final StreamExecutionEnvironment env =
        // StreamExecutionEnvironment.getExecutionEnvironment();
        //		PropertyConfigurator.configure("log4j.properties");

        final Configuration conf = new Configuration();
        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        // checkpointing is required for exactly-once or at-least-once guarantees

        //		env.enableCheckpointing(2000);

        // ====================== Source ========================
        final RMQConnectionConfig connectionConfig =
                new RMQConnectionConfig.Builder()
                        .setHost("localhost")
                        .setVirtualHost("/")
                        .setUserName("guest")
                        .setPassword("guest")
                        .setPort(5672)
                        //			.setPrefetchCount(1000)
                        .build();

        //		final DataStream<String> stream = env
        //			.addSource(new RMQSource<String>(
        //				connectionConfig,
        //				// config for the RabbitMQ connection
        //				"pub",
        //				// name of the RabbitMQ queue to consume
        //				true,
        //				// use correlation ids; can be false if only at-least-once is required
        //				new SimpleStringSchema()))   // deserialization schema to turn messages into Java
        // objects
        ////			.setParallelism(1);
        //		RabbitMQSource<String> rabbitMQSource = RabbitMQSource.
        //			<String>builder()
        //			.build(
        //				connectionConfig,
        //				"pub",
        //				new SimpleStringSchema(),
        //				AcknowledgeMode.AUTO
        //			);

        RabbitMQSource<String> rabbitMQSource =
                new RabbitMQSource<>(
                        connectionConfig,
                        "pub",
                        new SimpleStringSchema(),
                        ConsistencyMode.EXACTLY_ONCE);

        final DataStream<String> stream =
                env.fromSource(rabbitMQSource, WatermarkStrategy.noWatermarks(), "RabbitMQSource")
                        .setParallelism(1);

        DataStream<String> mappedMessages =
                stream.map(
                                (MapFunction<String, String>)
                                        message -> {
                                            System.out.println("Mapped" + message);
                                            return "Mapped: " + message;
                                        })
                        .setParallelism(10);

        // ====================== SINK ========================
        //		mappedMessages.addSink(new RMQSink<>(
        //			connectionConfig,            // config for the RabbitMQ connection
        //			"sub",                 // name of the RabbitMQ queue to send messages to
        //			new SimpleStringSchema()));  // serialization schema to turn Java objects to messages

        env.execute("RabbitMQ");
    }
}
