package com.arkivanov.mvikotlin.timetravel.proto.serialization

import com.arkivanov.mvikotlin.timetravel.proto.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.Value
import com.arkivanov.mvikotlin.timetravel.proto.Values
import com.arkivanov.mvikotlin.timetravel.proto.values

object TimeTravelStateUpdateDeserializer {

    fun deserialize(array: ByteArray): TimeTravelStateUpdate {
        val input = ByteArrayInputStream(array)

        return when (UpdateType.values[input.readInt()]) {
            UpdateType.FULL -> TimeTravelStateUpdate.Full(input.readState())
            UpdateType.UPDATE -> TimeTravelStateUpdate.Update(input.readState())
        }
    }

    private fun ByteArrayInputStream.readState(): TimeTravelState =
        TimeTravelState(
            events = readList { readEvent() },
            selectedEventIndex = readInt(),
            mode = TimeTravelState.Mode.values[readInt()]
        )

    private fun ByteArrayInputStream.readEvent(): TimeTravelEvent =
        TimeTravelEvent(
            id = readStringNotNull(),
            storeName = readStringNotNull(),
            type = StoreEventType.values[readInt()],
            value = readValues(),
            state = readValues()
        )

    private fun ByteArrayInputStream.readValues(): Values =
        values {
            repeat(readInt()) {
                put(readStringNotNull(), readValue())
            }
        }

    private fun ByteArrayInputStream.readValue(): Value =
        when (ValueType.values[readInt()]) {
            ValueType.INT -> Value.Int(readInt())
            ValueType.LONG -> Value.Long(readLong())
            ValueType.SHORT -> Value.Short(readShort())
            ValueType.BYTE -> Value.Byte(readByte())
            ValueType.FLOAT -> Value.Float(readFloat())
            ValueType.DOUBLE -> Value.Double(readDouble())
            ValueType.CHAR -> Value.Char(readChar())
            ValueType.BOOLEAN -> Value.Boolean(readBoolean())
            ValueType.OBJECT -> Value.Object(readString())
            ValueType.ARRAY -> Value.Array(readList { readValue() })
            ValueType.VALUES -> Value.Values(readValues())
        }

    private fun <T> ByteArrayInputStream.readList(reader: () -> T): List<T> =
        List(readInt()) { reader() }
}

