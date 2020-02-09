package com.hello.kafka.stream

import com.hello.kafka.Topic
import com.hello.kafka.Topics
import com.hello.kafka.start.createTopology
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

private fun getProps(): Properties {
    val props = Properties()
    props += StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
    props += StreamsConfig.APPLICATION_ID_CONFIG to "test"
    return props
}

class WordCounterProcessorTest {
    lateinit var testDriver: TopologyTestDriver
    lateinit var topology: Topology
    val sut = WordCounterProcessor()

    @BeforeAll
    fun init() {
        topology = createTopology(
            Topics(listOf(Topic("input"), Topic("output"))),
            sut
        )
        testDriver = TopologyTestDriver(topology, getProps())
    }

    @Test
    fun test() {

    }
}