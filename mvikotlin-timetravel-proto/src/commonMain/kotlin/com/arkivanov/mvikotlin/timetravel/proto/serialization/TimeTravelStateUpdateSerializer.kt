package com.arkivanov.mvikotlin.timetravel.proto.serialization

import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.Value
import com.arkivanov.mvikotlin.timetravel.proto.Values

object TimeTravelStateUpdateSerializer {

    fun serialize(update: TimeTravelStateUpdate): ByteArray {
        val output = ByteArrayOutputStream()

        output.writeInt(update.type.ordinal)

        when (update) {
            is TimeTravelStateUpdate.Full -> output.writeState(update.state)
            is TimeTravelStateUpdate.Update -> output.writeState(update.state)
        }.let {}

        return output.getBytes()
    }

    private val TimeTravelStateUpdate.type: UpdateType
        get() =
            when (this) {
                is TimeTravelStateUpdate.Full -> UpdateType.FULL
                is TimeTravelStateUpdate.Update -> UpdateType.UPDATE
            }

    private fun ByteArrayOutputStream.writeState(state: TimeTravelState) {
        writeCollection(state.events) { writeEvent(it) }
        writeInt(state.selectedEventIndex)
        writeInt(state.mode.ordinal)
    }

    private fun ByteArrayOutputStream.writeEvent(event: TimeTravelEvent) {
        writeString(event.id)
        writeString(event.storeName)
        writeInt(event.type.ordinal)
        writeValues(event.value)
        writeValues(event.state)
    }

    private fun ByteArrayOutputStream.writeValues(values: Values) {
        writeCollection(values.entries) {
            writeString(it.key)
            writeValue(it.value)
        }
    }

    private fun ByteArrayOutputStream.writeValue(value: Value) {
        writeInt(value.type.ordinal)

        when (value) {
            is Value.Int -> writeInt(value.value)
            is Value.Long -> writeLong(value.value)
            is Value.Short -> writeShort(value.value)
            is Value.Byte -> writeByte(value.value)
            is Value.Float -> writeFloat(value.value)
            is Value.Double -> writeDouble(value.value)
            is Value.Char -> writeChar(value.value)
            is Value.Boolean -> writeBoolean(value.value)
            is Value.Object -> writeString(value.value)
            is Value.Array -> writeCollection(value.value) { writeValue(it) }
            is Value.Values -> writeValues(value.value)
        }.let {}
    }

    private inline fun <T> ByteArrayOutputStream.writeCollection(collection: Collection<T>, writer: (T) -> Unit) {
        writeInt(collection.size)
        collection.forEach(writer)
    }

    private val Value.type: ValueType
        get() =
            when (this) {
                is Value.Int -> ValueType.INT
                is Value.Long -> ValueType.LONG
                is Value.Short -> ValueType.SHORT
                is Value.Byte -> ValueType.BYTE
                is Value.Float -> ValueType.FLOAT
                is Value.Double -> ValueType.DOUBLE
                is Value.Char -> ValueType.CHAR
                is Value.Boolean -> ValueType.BOOLEAN
                is Value.Object -> ValueType.OBJECT
                is Value.Array -> ValueType.ARRAY
                is Value.Values -> ValueType.VALUES
            }
}

