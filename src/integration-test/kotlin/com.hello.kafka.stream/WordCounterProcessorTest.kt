package com.hello.kafka.stream

import com.hello.kafka.Topic
import com.hello.kafka.Topics
import com.hello.kafka.start.createTopology
import com.hello.kafka.stream.support.TopologyTest
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Properties
import java.util.concurrent.TimeUnit


private fun getProps(): Properties {
    val props = Properties()
    props += StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
    props += StreamsConfig.APPLICATION_ID_CONFIG to "test"
    props += StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name
    props += StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name
    return props
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WordCounterProcessorTest() : TopologyTest() {

    val inputTopicName = "test-input"
    val outputTopicName = "test-output"
    val sut = WordCounterProcessor()
    val topics = Topics(
            listOf(
                    Topic(inputTopicName, 1, 1),
                    Topic(outputTopicName, 1, 1)
            )
    )
    override val topology = createTopology(topics, sut)
    override val props: Properties = getProps()

    @Test
    fun test() {
        val streams = KafkaStreams(topology, props)
        streams.start()

        produce(inputTopicName, "a","a")
        produce(inputTopicName,"a","b")
        produce(inputTopicName,"a","c")
        produce(inputTopicName,"a","a")
        produce(inputTopicName,"a","a")

        TimeUnit.SECONDS.sleep(5L)
        repeat(5) {
            println(consume(outputTopicName))
        }
    }

}
