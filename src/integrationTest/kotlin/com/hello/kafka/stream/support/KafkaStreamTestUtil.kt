package com.hello.kafka.stream.support

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.time.Duration
import java.util.Properties

class KafkaStreamTestUtil(
        topology: Topology,
        props: Properties,
        inputTopicName: String,
        outputTopicName: String
) : AfterAllCallback {

    private val testDriver: TopologyTestDriver = TopologyTestDriver(topology, props)
    private val input = testDriver.createInputTopic(inputTopicName, StringSerializer(), StringSerializer())
    private val output = testDriver.createOutputTopic(outputTopicName, StringDeserializer(), StringDeserializer())

    override fun afterAll(context: ExtensionContext?) {
        testDriver.close()
    }

    fun produce(key: String? = null, value: String) =
        input.pipeInput(key, value)

    fun consume(): MutableList<KeyValue<String, String>> =
        output.readKeyValuesToList()

    fun advanceClock(time: Duration) =
        input.advanceTime(time)

    fun <V> getStateStore(storeName: String): KeyValueStore<String, V> =
        testDriver.getKeyValueStore(storeName)
}
