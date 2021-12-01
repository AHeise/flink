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

package org.apache.flink.fs.s3hadoop;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Testcontainers
class S3ADeleteITCase {
    private static final Logger LOG = LoggerFactory.getLogger(S3ADeleteITCase.class);

//    @Container
//    private final MinioContainer minio =
//            new MinioContainer().withLogConsumer(new Slf4jLogConsumer(LOG));

    ForkJoinPool pool = new ForkJoinPool(100);

    @Test
    void testDirectoryDelete() throws IOException {
//        Path path = minio.getFullPath("path");
        Path path = new Path("s3://flink-integration-tests/temp/delete-performance-hadoop2");

        System.out.println("started");
//        Configuration config = minio.getConfig();
        Configuration conf = new Configuration();
        conf.setString("s3.endpoint", "s3.us-east-1.amazonaws.com");
//        conf.setString("s3.endpoint", getHostAddress());
        conf.setString("s3.path.style.access", "true");
        conf.setString("s3.path-style-access", "true");
        FileSystem.initialize(conf, null);
        FileSystem fs = FileSystem.get(URI.create(path.toString()));

        for (int numFiles = 10; numFiles <= 100000; numFiles *= 10) {
            int finalNumFiles = numFiles;
            long addTime = measure(() -> addFiles(path, fs, finalNumFiles));
            FileStatus[] status = fs.listStatus(path);
            long deleteTime = measure(() -> fs.delete(path, true));
            System.out.printf("%s;%s;%s;%s\n", numFiles, addTime, deleteTime, status.length);
        }
    }

    private void addFiles(Path path, FileSystem fs, int finalNumFiles) {
        int parallelism = Math.min(finalNumFiles,  pool.getParallelism());
        List<? extends ForkJoinTask<?>> tasks = IntStream.range(0, parallelism)
                .mapToObj(step ->
                        pool.submit(() -> IntStream.range(0, finalNumFiles / parallelism).
                                forEach(i -> addFile(path, fs, i * parallelism + step))))
                .collect(Collectors.toList());
        tasks.forEach(t -> t.join());
    }

    private void addFile(Path path, FileSystem fs, int i) {
        try (FSDataOutputStream out =
                     fs.create(new Path(path, i + ".txt"), FileSystem.WriteMode.OVERWRITE)) {
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private long measure(ThrowingRunnable<IOException> runnable) throws IOException {
        final long begin = System.currentTimeMillis();
        runnable.run();
        final long end = System.currentTimeMillis();
        return end - begin;
    }
}
