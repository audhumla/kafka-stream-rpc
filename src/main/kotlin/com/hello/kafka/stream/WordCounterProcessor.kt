package com.hello.kafka.stream

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration

class WordCounterProcessor : Processor<String, String> {

    val name = "WordCounterProcessor"
    val stateStoreName = "Counts"
    private lateinit var kvStore: KeyValueStore<String, String>
    private lateinit var context: ProcessorContext

    @SuppressWarnings("unchecked")
    override fun init(context: ProcessorContext?) {
        if (context != null) {
            this.context = context
            this.kvStore = context.getStateStore(stateStoreName) as KeyValueStore<String, String>
        }
    }

    override fun process(key: String?, value: String?) {
        value
            ?.splitInWords()
            ?.forEach { word ->
                val newCount = incrementCountInStateStore(word)
                context.forward(word, newCount)
                context.commit()
            }
    }

    private fun String.splitInWords() =
        toLowerCase()
            .split(regex = "\\W+".toRegex())
            .stream()


    private fun incrementCountInStateStore(word: String): String {
        val count = synchronized(kvStore) {
            kvStore.incrementCount(word)
        }
        return count.toString().also {
            kvStore.put(word, it)
        }
    }

    private fun  KeyValueStore<String, String>.incrementCount(word: String) =
        get(word)
            ?.toInt()
            ?.let {
                it + 1
            } ?: 1

    @SuppressWarnings("unused")
    private fun scheduleStoreDeletionEveryMinute() {
        context.let { ctx ->
            ctx.schedule(
                Duration.ofMinutes(1),
                PunctuationType.STREAM_TIME
            ) { _ ->
                kvStore.all().use { keys ->
                    keys.forEachRemaining {
                        kvStore.delete(it.key)
                    }
                }
                ctx.commit()
            }
        }
    }

    override fun close() {
        // Nothing to do
    }
}
