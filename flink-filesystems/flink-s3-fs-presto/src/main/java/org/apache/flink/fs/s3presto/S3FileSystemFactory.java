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

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.facebook.presto.hive.s3.HackyPrestoS3FileSystem;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.core.fs.Path;
import org.apache.flink.fs.s3.common.AbstractS3FileSystemFactory;
import org.apache.flink.runtime.util.HadoopConfigLoader;
import org.apache.flink.util.FlinkRuntimeException;

import com.facebook.presto.hive.s3.PrestoS3FileSystem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/** Simple factory for the S3 file system. */
public class S3FileSystemFactory extends AbstractS3FileSystemFactory<HackyPrestoS3FileSystem> {

    private static final String[] FLINK_CONFIG_PREFIXES = {"s3.", "presto.s3."};

    private static final String[][] MIRRORED_CONFIG_KEYS = {
        {"presto.s3.access.key", "presto.s3.access-key"},
        {"presto.s3.secret.key", "presto.s3.secret-key"},
        {"presto.s3.path.style.access", "presto.s3.path-style-access"}
    };

    public S3FileSystemFactory() {
        super("Presto S3 File System", createHadoopConfigLoader());
    }

    @Override
    public String getScheme() {
        return "s3";
    }

    @VisibleForTesting
    static HadoopConfigLoader createHadoopConfigLoader() {
        return new HadoopConfigLoader(
                FLINK_CONFIG_PREFIXES,
                MIRRORED_CONFIG_KEYS,
                "presto.s3.",
                Collections.emptySet(),
                Collections.emptySet(),
                "");
    }

    @Override
    protected HackyPrestoS3FileSystem createHadoopFileSystem() {
        return new HackyPrestoS3FileSystem();
    }

    @Override
    protected void bulkDelete(HackyPrestoS3FileSystem fs, Collection<Path> paths) throws IOException {
        fs.bulkDelete(paths.stream().map(path -> path.getPath().substring(1)).collect(Collectors.toList()));
    }

    @Override
    protected URI getInitURI(URI fsUri, org.apache.hadoop.conf.Configuration hadoopConfig) {
        final String scheme = fsUri.getScheme();
        final String authority = fsUri.getAuthority();
        final URI initUri;

        if (scheme == null && authority == null) {
            initUri = createURI("s3://s3.amazonaws.com");
        } else if (scheme != null && authority == null) {
            initUri = createURI(scheme + "://s3.amazonaws.com");
        } else {
            initUri = fsUri;
        }
        return initUri;
    }

    private URI createURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new FlinkRuntimeException("Error in s3 aws URI - " + str, e);
        }
    }

}
