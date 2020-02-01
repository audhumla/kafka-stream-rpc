package com.hello.kafka.stream

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Produced


class WordCounterStreamDsl(
        private val inputTopic: String,
        private val outputTopic: String
) {

    private val ktable = StreamsBuilder().stream<String, String>(inputTopic)
            .flatMapValues { value -> splitterByLine(value) }
            .groupBy { _, value -> value }
            .count()

    fun start() {
        ktable.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()))
    }

}

val splitterByLine : (s: String) -> List<String> = { str -> str.split("\\W+".toRegex())}
