package com.arkivanov.mvikotlin.timetravel.proto

typealias Values = Map<String, Value>

inline fun values(builder: MutableMap<String, Value>.() -> Unit): Values =
    mutableMapOf<String, Value>().also(builder)
