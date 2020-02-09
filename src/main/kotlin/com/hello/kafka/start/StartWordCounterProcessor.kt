package com.hello.kafka.start

import com.hello.kafka.Topics
import com.hello.kafka.createTopics
import com.hello.kafka.stream.WordCounterProcessor
import com.hello.kafka.wait
import com.sksamuel.hoplite.ConfigLoader
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores

fun main() {
    val topics = ConfigLoader().loadConfigOrThrow<Topics>("/topics.yml")
    val props = "/kafkastream.properties".loadProps()

    createTopics(props, topics) wait 60

    val processor = WordCounterProcessor()
    val topology = createTopology(topics, processor)
    val streams = KafkaStreams(topology, props)

    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        streams.cleanUp()
        streams.close()
    })
}

fun createTopology(topics: Topics, processor: WordCounterProcessor) =
    Topology()
        .addSource("input-topic", topics.topics[0].name)
        .addProcessor(processor.name, ProcessorSupplier<String, String> { processor })
        .addStateStore(inMemoryStateStore(), processor.name)
        // add the sink processor node that takes Kafka topic "sink-topic" as output
        // and the WordCountProcessor node as its upstream processor
        .addSink("sink-processor", topics.topics[1].name, processor.name)

fun inMemoryStateStore(): StoreBuilder<KeyValueStore<String, Long>> =
        Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore("Counts"),
                Serdes.String(),
                Serdes.Long()
            )
            .withLoggingDisabled() // disable backing up the store to a changelog topic
