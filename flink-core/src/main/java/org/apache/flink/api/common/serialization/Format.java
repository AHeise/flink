package org.apache.flink.connector.file;

import org.apache.flink.api.common.serialization.BulkWriter.Factory;
import org.apache.flink.api.common.serialization.Encoder;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configurable;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.reader.BulkFormat;
import org.apache.flink.connector.file.src.reader.StreamFormat;

public interface Format<T, SELF extends Format<T, SELF>> extends Configurable<SELF> {

    interface WithBulkWriter<T, SELF extends Format<T, SELF>> extends Format<T, SELF> {
        Factory<T> asWriter();
    }

    interface WithEncoder<T, SELF extends Format<T, SELF>> extends Format<T, SELF> {
        Encoder<T> asEncoder();
    }

    interface WithTypeInformation<T, SELF extends WithTypeInformation<T, SELF>> extends Format<T, SELF> {
        TypeInformation<T> getTypeInformation();
    }

    interface WithStreamFormat<T, SELF extends WithStreamFormat<T, SELF>> extends WithTypeInformation<T, SELF> {
        StreamFormat<T> asStreamFormat();
    }

    interface WithBulkFormat<T, SELF extends WithBulkFormat<T, SELF>> extends WithTypeInformation<T, SELF> {
        <SplitT extends FileSourceSplit> BulkFormat<T, SplitT> asBulkFormat();
    }
}
