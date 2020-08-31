package com.hello.kafka.start

import java.util.Properties

fun String.asURL() = this.javaClass::class.java.getResource(this)!!

fun loadProps(filename: String): Properties =
    filename.asURL().openStream().use {
            val props = Properties()
            props.load(it)
            return props
        }
