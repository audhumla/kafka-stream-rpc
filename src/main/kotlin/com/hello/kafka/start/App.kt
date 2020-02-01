package com.hello.kafka.start

import com.hello.kafka.Topics
import com.hello.kafka.createTopics
import com.hello.kafka.stream.WordCounterKafkaStreamStateStore
import com.hello.kafka.wait
import com.sksamuel.hoplite.ConfigLoader
import java.util.Properties

fun main() {
    val topics = ConfigLoader().loadConfigOrThrow<Topics>("/topics.yml")
    val props = "/kafkastream.properties".loadProps()

    createTopics(props, topics) wait 60

    val stream = WordCounterKafkaStreamStateStore(props, topics.first().name)
    stream.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        stream.cleanUp()
        stream.stop()
    })
}


fun String.asURL() = this.javaClass::class.java.getResource(this)!!

fun String.loadProps(): Properties =
        asURL().openStream().use {
            val props = Properties()
            props.load(it)
            return props
        }
