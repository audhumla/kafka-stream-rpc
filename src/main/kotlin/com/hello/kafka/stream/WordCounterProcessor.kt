package com.hello.kafka.stream

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.StateStore
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration

class WordCounterProcessor : Processor<String, String> {

    val name = "WordCounterProcessor"
    val stateStoreName = "Counts"
    private lateinit var kvStore: KeyValueStore<String, String>

    override fun init(context: ProcessorContext?) {
        if (context != null) {
            kvStore = context.getStateStore(stateStoreName) as KeyValueStore<String, String>
        }

        context?.let { ctx ->
            ctx.schedule(
                Duration.ofSeconds(3),
                PunctuationType.STREAM_TIME
            ) { _ ->
                kvStore.all().use { iter ->
                    iter.forEachRemaining {
                        ctx.forward(it.key, it.value)
                        kvStore.delete(it.key)
                    }
                }
                ctx.commit()
            }
        }

    }

    override fun process(key: String?, value: String?) {
        println("$key: $value")
        value
            ?.toLowerCase()
            ?.split(regex = "\\W+".toRegex())
            ?.stream()
            ?.forEach { word ->
                val count = kvStore.get(word)?.let { it + 1 } ?: 1
                kvStore.put(word, count.toString());
            }
    }

    override fun close() {
        // Nothing to do
    }
}
