dependencies {
    implementation(project(":flink-examples:flink-examples-streaming"))
}

description = "flink-examples-streaming-twitter"

flinkSetMainClass("org.apache.flink.streaming.examples.twitter.TwitterExample")