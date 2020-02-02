package com.hello.kafka.stream

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import java.util.Properties

private fun getProps(): Properties {
    val props = Properties()
    props += StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
    props += StreamsConfig.APPLICATION_ID_CONFIG to "test"
    return props
}