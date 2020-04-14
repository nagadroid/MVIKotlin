package com.arkivanov.mvikotlin.timetravel.proto

sealed class Value {

    data class Int(val value: kotlin.Int) : Value()
    data class Long(val value: kotlin.Long) : Value()
    data class Short(val value: kotlin.Short) : Value()
    data class Byte(val value: kotlin.Byte) : Value()
    data class Float(val value: kotlin.Float) : Value()
    data class Double(val value: kotlin.Double) : Value()
    data class Char(val value: kotlin.Char) : Value()
    data class Boolean(val value: kotlin.Boolean) : Value()
    data class Object(val value: String?) : Value()
    data class Array(val value: List<Value>) : Value()
    data class Values(val value: com.arkivanov.mvikotlin.timetravel.proto.Values) : Value()
}
