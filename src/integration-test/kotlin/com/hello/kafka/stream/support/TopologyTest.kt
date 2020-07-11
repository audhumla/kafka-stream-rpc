package com.hello.kafka.stream.support

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.header.internals.RecordHeaders
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.util.*

abstract class TopologyTest {

    lateinit var testDriver: TopologyTestDriver

    abstract val props: Properties

    abstract val topology: Topology

    @BeforeAll
    fun setUp() {
        testDriver = TopologyTestDriver(topology, props)
    }

    @AfterAll
    fun cleanUp() {
        testDriver.close()
    }

    fun produce(topic: String, key: String, value: String, headers: Map<String, String> = emptyMap()) {
        val factory: ConsumerRecordFactory<String, String> =
                ConsumerRecordFactory(topic, StringSerializer(), StringSerializer())

        val avroHeaders = RecordHeaders(headers.map { e -> RecordHeader(e.key, e.value.toByteArray(Charsets.UTF_8)) })

        testDriver.pipeInput(factory.create(topic, key, value, avroHeaders))
    }

    fun consume(topic: String): ProducerRecord<String, String>? {
        return testDriver.readOutput(topic, StringDeserializer(), StringDeserializer())
    }

    inline fun <reified V> getStateStore(storeName: String): KeyValueStore<String, V> {
        return this.testDriver.getKeyValueStore(storeName)
    }
}
