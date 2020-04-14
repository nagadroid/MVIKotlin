package com.arkivanov.mvikotlin.timetravel.proto.serialization

internal enum class UpdateType {
    FULL, UPDATE;

    companion object {
        val values: List<UpdateType> = values().toList()
    }
}

internal enum class ValueType {
    INT, LONG, SHORT, BYTE, FLOAT, DOUBLE, CHAR, BOOLEAN, OBJECT, ARRAY, VALUES;

    companion object {
        val values: List<ValueType> = values().toList()
    }
}
