package com.hello.kafka.stream

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.WindowStore
import java.time.Duration
import java.util.*


class WordCounterKafkaStreamStateStore(
    private val props: Properties,
    private val inputTopic: String,
    private val stateStoreName: String = "wordCounterStateStore",
    private val timeWindowForWordCount: Duration = Duration.ofMinutes(1L)
) {
    private val stringSerde = Serdes.String()
    private lateinit var kafkaStreams: KafkaStreams

    fun start() {
        val builder = StreamsBuilder()
        builder.stream(inputTopic, Consumed.with(stringSerde, stringSerde))
                .flatMapValues { value -> splitterByLine(value) }
                .groupBy{ _, word -> word }
                .windowedBy(TimeWindows.of(timeWindowForWordCount))
                .count(
                        Materialized
                                .`as`<String, Long, WindowStore<Bytes, ByteArray>>(stateStoreName)
                                .withValueSerde(Serdes.Long())
                )
        kafkaStreams = KafkaStreams(builder.build(), props)
    }

    fun stop() {
        if (::kafkaStreams.isInitialized)
            kafkaStreams.close()
    }

    fun cleanUp() {
        if (::kafkaStreams.isInitialized)
            kafkaStreams.cleanUp()
    }

}
