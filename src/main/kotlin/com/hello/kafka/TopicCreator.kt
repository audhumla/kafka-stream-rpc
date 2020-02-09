package com.hello.kafka

import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS

fun createTopics(
        props: Properties,
        topics: Topics
): CreateTopicsResult {
    val topicsToCreate = topics.map {
        NewTopic(it.name, it.partition, it.replica)
    }

    return KafkaAdminClient.create(props).use {
        it.createTopics(topicsToCreate)
    }
}

data class Topic(val name: String, val partition: Int = 1, val replica: Short = 1)
data class Topics(val topics: List<Topic>): Iterable<Topic> by topics

infix fun CreateTopicsResult.wait(seconds: Long) {
    this.all().get(seconds, SECONDS)
}
