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

package com.facebook.presto.hive.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.google.common.collect.Iterables;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Grants access to the underlying s3 client. */
public class HackyPrestoS3FileSystem extends PrestoS3FileSystem {
    private static final int MAX_DELETES = 1000;
    private String bucketName;
    private AmazonS3 s3;

    @Override
    public void initialize(URI uri, Configuration conf) throws IOException {
        super.initialize(uri, conf);
        bucketName = getBucketName(uri);
        s3 = getS3Client();
    }

    public void bulkDelete(Collection<String> keys) throws IOException {
        try {
            for (List<String> partition : Iterables.partition(keys, MAX_DELETES)) {
                s3.deleteObjects(
                        new DeleteObjectsRequest(bucketName)
                                .withKeys(
                                        partition.stream()
                                                .map(DeleteObjectsRequest.KeyVersion::new)
                                                .collect(Collectors.toList()))
                                .withQuiet(true));
            }
        } catch (SdkClientException e) {
            throw new IOException("Cannot delete all files", e);
        }
    }
}
