package com.arkivanov.mvikotlin.timetravel.proto.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ByteArrayInputOutputTest {

    @Test
    fun read_write() {
        val output = ByteArrayOutputStream()
        repeat(10) {
            output.writeTestData()
        }
        val array = output.getBytes()

        val input = ByteArrayInputStream(array)

        repeat(10) {
            input.readAndVerifyTestData()
        }
    }

    private fun ByteArrayOutputStream.writeTestData() {
        writeInt(Int.MAX_VALUE)
        writeInt(0)
        writeInt(Int.MIN_VALUE)
        writeInt(357)

        writeLong(Long.MAX_VALUE)
        writeLong(0)
        writeLong(Long.MIN_VALUE)

        writeShort(Short.MAX_VALUE)
        writeShort(0)
        writeShort(Short.MIN_VALUE)

        writeByte(Byte.MAX_VALUE)
        writeByte(0)
        writeByte(Byte.MIN_VALUE)

        writeFloat(Float.MAX_VALUE)
        writeFloat(0F)
        writeFloat(Float.MIN_VALUE)
        writeFloat(Float.POSITIVE_INFINITY)
        writeFloat(Float.NEGATIVE_INFINITY)
        writeFloat(Float.NaN)

        writeDouble(Double.MAX_VALUE)
        writeDouble(0.0)
        writeDouble(Double.MIN_VALUE)
        writeDouble(Double.POSITIVE_INFINITY)
        writeDouble(Double.NEGATIVE_INFINITY)
        writeDouble(Double.NaN)

        writeChar(Char.MAX_VALUE)
        writeChar(0.toChar())
        writeChar(Char.MIN_VALUE)

        writeBoolean(false)
        writeBoolean(true)

        writeString(null)
        writeString("")
        writeString("String Строка")
    }

    private fun ByteArrayInputStream.readAndVerifyTestData() {
        assertEquals(Int.MAX_VALUE, readInt())
        assertEquals(0, readInt())
        assertEquals(Int.MIN_VALUE, readInt())
        assertEquals(357, readInt())

        assertEquals(Long.MAX_VALUE, readLong())
        assertEquals(0L, readLong())
        assertEquals(Long.MIN_VALUE, readLong())

        assertEquals(Short.MAX_VALUE, readShort())
        assertEquals(0, readShort())
        assertEquals(Short.MIN_VALUE, readShort())

        assertEquals(Byte.MAX_VALUE, readByte())
        assertEquals(0, readByte())
        assertEquals(Byte.MIN_VALUE, readByte())

        assertEquals(Float.MAX_VALUE, readFloat())
        assertEquals(0F, readFloat())
        assertEquals(Float.MIN_VALUE, readFloat())
        assertEquals(Float.POSITIVE_INFINITY, readFloat())
        assertEquals(Float.NEGATIVE_INFINITY, readFloat())
        assertEquals(Float.NaN, readFloat())

        assertEquals(Double.MAX_VALUE, readDouble())
        assertEquals(0.0, readDouble())
        assertEquals(Double.MIN_VALUE, readDouble())
        assertEquals(Double.POSITIVE_INFINITY, readDouble())
        assertEquals(Double.NEGATIVE_INFINITY, readDouble())
        assertEquals(Double.NaN, readDouble())

        assertEquals(Char.MAX_VALUE, readChar())
        assertEquals(0.toChar(), readChar())
        assertEquals(Char.MIN_VALUE, readChar())

        assertFalse(readBoolean())
        assertTrue(readBoolean())

        assertNull(readString())
        assertEquals("", readString())
        assertEquals("String Строка", readString())
    }
}
