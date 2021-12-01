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

package org.apache.flink.fs.s3presto;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.apache.flink.util.Preconditions.checkState;

public class MinioContainer extends GenericContainer<MinioContainer> {
    private static final int DEFAULT_PORT = 9000;
    private static final DockerImageName DEFAULT_IMAGE =
            DockerImageName.parse("minio/minio:RELEASE.2021-11-09T03-21-45Z.fips");
    private static final String ACCESS_KEY = "ACCESS_KEY";
    private static final String SECRET_KEY = "SECRET_KEY";

    private static final String HEALTH_ENDPOINT = "/minio/health/ready";

    private static final String DEFAULT_BUCKET = "bucket";
    private AmazonS3 client;

    public MinioContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        addExposedPort(DEFAULT_PORT);
        withEnv("MINIO_ACCESS_KEY", ACCESS_KEY);
        withEnv("MINIO_SECRET_KEY", SECRET_KEY);
        withEnv("MINIO_DOMAIN", "localhost");
        withCommand("server", "/data");
        setWaitStrategy(
                new HttpWaitStrategy()
                        .forPort(DEFAULT_PORT)
                        .forPath(HEALTH_ENDPOINT)
                        .withStartupTimeout(Duration.ofMinutes(2)));
    }

    public MinioContainer() {
        this(DEFAULT_IMAGE);
    }

    public String getHostAddress() {
        return "http://" + getContainerIpAddress() + ":" + getMappedPort(DEFAULT_PORT);
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);

        client =
                AmazonS3ClientBuilder.standard()
                        .withCredentials(
                                new AWSStaticCredentialsProvider(
                                        new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                        .withPathStyleAccessEnabled(true)
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(
                                        getHostAddress(), Regions.US_EAST_1.name()))
                        .build();
        client.createBucket(DEFAULT_BUCKET);
    }

    public static String getAccessKey() {
        return ACCESS_KEY;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public Configuration getConfig() {
        final Configuration conf = new Configuration();
        conf.setString("s3.access.key", getAccessKey());
        conf.setString("s3.secret.key", getSecretKey());
        conf.setString("s3.endpoint", getHostAddress());
        conf.setString("s3.path.style.access", "true");
        conf.setString("s3.path-style-access", "true");
        return conf;
    }

    public static String getDefaultBucket() {
        return DEFAULT_BUCKET;
    }

    public AmazonS3 getClient() {
        checkState(client != null, "Can only access client after the container is started");
        return client;
    }

    public Path getFullPath(String path) {
        return new Path("s3://" + DEFAULT_BUCKET + "/" + path);
    }
}
