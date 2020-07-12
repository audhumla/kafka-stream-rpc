package com.hello.kafka.stream.support

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.time.Duration
import java.util.*

class KafkaStreamTestUtil(
        val topology: Topology,
        val props: Properties,
        val inputTopicName: String,
        val outputTopicName: String
) : AfterAllCallback {

    val testDriver: TopologyTestDriver = TopologyTestDriver(topology, props)
    val input = testDriver.createInputTopic(inputTopicName, StringSerializer(), StringSerializer())
    val output = testDriver.createOutputTopic(outputTopicName, StringDeserializer(), StringDeserializer())

    override fun afterAll(context: ExtensionContext?) {
        testDriver.close()
    }

    fun produce(key: String, value: String) = input.pipeInput(key, value)

    fun consume() = output.readKeyValuesToList()

    fun advanceClock(time: Duration) = input.advanceTime(time)

    inline fun <reified V> getStateStore(storeName: String): KeyValueStore<String, V> {
        return this.testDriver.getKeyValueStore(storeName)
    }
}
