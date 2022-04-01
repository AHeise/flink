package org.apache.flink.connector.file;

import org.apache.flink.api.common.serialization.Format;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.reader.BulkFormat;
import org.apache.flink.connector.file.src.reader.StreamFormat;

public interface FileFormat<T, SELF extends FileFormat<T, SELF>> extends Format<T, SELF> {

    interface WithStreamFormat<T, SELF extends WithStreamFormat<T, SELF>> extends WithTypeInformation<T, SELF> {
        StreamFormat<T> asStreamFormat();
    }

    interface WithBulkFormat<T, SELF extends WithBulkFormat<T, SELF>> extends WithTypeInformation<T, SELF> {
        <SplitT extends FileSourceSplit> BulkFormat<T, SplitT> asBulkFormat();
    }
}
