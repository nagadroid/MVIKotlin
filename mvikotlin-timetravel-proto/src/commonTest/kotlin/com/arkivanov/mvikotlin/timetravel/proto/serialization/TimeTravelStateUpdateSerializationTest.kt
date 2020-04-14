package com.arkivanov.mvikotlin.timetravel.proto.serialization

import com.arkivanov.mvikotlin.timetravel.proto.StoreEventType
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.timetravel.proto.Value
import com.arkivanov.mvikotlin.timetravel.proto.Values
import com.arkivanov.mvikotlin.timetravel.proto.values
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTravelStateUpdateSerializationTest {

    @Test
    fun serializes_full() {
        val written = TimeTravelStateUpdate.Full(state())

        val array = TimeTravelStateUpdateSerializer.serialize(written)
        val read = TimeTravelStateUpdateDeserializer.deserialize(array)

        assertEquals(written, read)
    }

    @Test
    fun serializes_update() {
        val written = TimeTravelStateUpdate.Update(state())

        val array = TimeTravelStateUpdateSerializer.serialize(written)
        val read = TimeTravelStateUpdateDeserializer.deserialize(array)

        assertEquals(written, read)
    }

    private fun state(): TimeTravelState =
        TimeTravelState(
            events = listOf(
                TimeTravelEvent(
                    id = "event1",
                    storeName = "store1",
                    type = StoreEventType.INTENT,
                    value = values(),
                    state = values()
                )
            ),
            selectedEventIndex = 5,
            mode = TimeTravelState.Mode.RECORDING
        )

    private fun values(): Values =
        values {
            put("int", Value.Int(123))
            put("long", Value.Long(234L))
            put("short", Value.Short(345))
            put("byte", Value.Short(32))
            put("float", Value.Float(543F))
            put("double", Value.Double(763.0))
            put("char", Value.Char('f'))
            put("boolean1", Value.Boolean(true))
            put("boolean2", Value.Boolean(false))
            put("object1", Value.Object(null))
            put("object2", Value.Object(""))
            put("object3", Value.Object("object3"))
            put("array", Value.Array(listOf(Value.Int(0), Value.Long(1L))))

            put(
                "values",
                Value.Values(
                    values {
                        put("v_int", Value.Int(0))
                        put("v_long", Value.Long(0L))
                    }
                )
            )
        }
}
