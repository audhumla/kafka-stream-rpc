package com.hello.kafka.stream

import com.hello.kafka.start.createTopology
import com.hello.kafka.stream.support.KafkaStreamTestUtil
import com.hello.kafka.toTopics
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Properties


private fun getProps(): Properties {
    val props = Properties()
    props += StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
    props += StreamsConfig.APPLICATION_ID_CONFIG to "test"
    props += StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name
    props += StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name
    return props
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WordCounterProcessorTest {
    val inputTopicName = "test-input"
    val outputTopicName = "test-output"
    val sut = WordCounterProcessor()
    val topics = listOf(inputTopicName, outputTopicName).toTopics()
    val topology = createTopology(topics, sut)
    val props: Properties = getProps()
    val testUtil = KafkaStreamTestUtil(topology, props, inputTopicName, outputTopicName)
    val streams = KafkaStreams(topology, props)
    val kvStore = testUtil.getStateStore<String>("Counts")

    @BeforeAll
    fun init() {
        streams.start()
    }

    @AfterAll
    fun afterAll() {
        streams.close()
        testUtil.afterAll(null)
    }

    @Test
    fun `should increment the counter in the state store`() {
        repeat(10) {
            testUtil.produce(value = "test-message")
        }

        val count = kvStore.get("test")

        assertThat(count).isEqualTo("10")
    }
}
