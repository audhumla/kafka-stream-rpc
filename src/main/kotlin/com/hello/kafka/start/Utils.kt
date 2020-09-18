package com.hello.kafka.start

import com.hello.kafka.stream.WordCounterProcessor
import java.io.File
import java.net.URL
import java.util.Objects
import java.util.Properties

fun asURL(filename: String): URL {
    require(Objects.nonNull(filename)) { "Cannot convert null string to URL" }
    require(File(filename).exists()) { "File $filename does not exist" }
    return WordCounterProcessor::class.java.getResource(filename)!!
}

fun loadProps(filename: String): Properties {
    return asURL(filename).openStream().use {
        val props = Properties()
        props.load(it)
        props
    }
}