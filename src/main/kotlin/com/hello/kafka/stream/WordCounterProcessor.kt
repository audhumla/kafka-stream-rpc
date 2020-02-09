package com.hello.kafka.stream

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration

class WordCounterProcessor : Processor<String, String> {

    val name = "WordCounterProcessor"
    private lateinit var kvStore: KeyValueStore<String, Long>
    private lateinit var processorContext: ProcessorContext

    override fun init(context: ProcessorContext?) {
        if (context != null) {
            processorContext = context
            kvStore = context.getStateStore("Counts") as KeyValueStore<String, Long>
        }

        context?.let { context ->
            context.schedule(
                Duration.ofMinutes(1),
                PunctuationType.STREAM_TIME
            ) { _ ->
                kvStore.all().use { iter ->
                    iter.forEachRemaining {
                        processorContext.forward(it.key, it.value)
                        kvStore.delete(it.key)
                    }
                }
                processorContext.commit()
            }
        }

    }

    override fun process(key: String?, value: String?) {
        value
            ?.toLowerCase()
            ?.split(regex = "\\W+".toRegex())
            ?.stream()
            ?.forEach { word ->
                val count = kvStore.get(word)?.let { it + 1 } ?: 1
                kvStore.put(word, count);
            }
    }

    override fun close() {
        // Nothing to do
    }
}