package com.arkivanov.mvikotlin.timetravel.proto.serialization

@UseExperimental(ExperimentalUnsignedTypes::class)
internal class ByteArrayOutputStream {

    private var array = ByteArray(8)
    private var position = 0

    fun getBytes(): ByteArray = array.copyOf(position)

    fun writeInt(value: Int) {
        writeUByte((value and 0xFF).toUByte())
        writeUByte(((value shr 8) and 0xFF).toUByte())
        writeUByte(((value shr 16) and 0xFF).toUByte())
        writeUByte(((value shr 24) and 0xFF).toUByte())
    }

    fun writeLong(value: Long) {
        writeUByte((value and 0xFF).toUByte())
        writeUByte(((value shr 8) and 0xFF).toUByte())
        writeUByte(((value shr 16) and 0xFF).toUByte())
        writeUByte(((value shr 24) and 0xFF).toUByte())
        writeUByte(((value shr 32) and 0xFF).toUByte())
        writeUByte(((value shr 40) and 0xFF).toUByte())
        writeUByte(((value shr 48) and 0xFF).toUByte())
        writeUByte(((value shr 56) and 0xFF).toUByte())
    }

    fun writeShort(value: Short) {
        writeUByte((value.toInt() and 0xFF).toUByte())
        writeUByte(((value.toInt() shr 8) and 0xFF).toUByte())
    }

    fun writeByte(value: Byte) {
        ensureSpace(1)
        array[position++] = value
    }

    fun writeFloat(value: Float) {
        writeInt(value.toRawBits())
    }

    fun writeDouble(value: Double) {
        writeLong(value.toRawBits())
    }

    fun writeChar(value: Char) {
        writeShort(value.toShort())
    }

    fun writeBoolean(value: Boolean) {
        writeByte(if (value) 1 else 0)
    }

    @UseExperimental(ExperimentalStdlibApi::class)
    fun writeString(s: String?) {
        if (s == null) {
            writeByte(0)
            return
        }

        if (s.isEmpty()) {
            writeByte(1)
            return
        }

        writeByte(2)
        write(s.encodeToByteArray())
    }

    private fun write(bytes: ByteArray) {
        writeInt(bytes.size)
        ensureSpace(bytes.size)
        bytes.copyInto(destination = array, destinationOffset = position)
        position += bytes.size
    }

    private fun writeUByte(byte: UByte) {
        writeByte(byte.toByte())
    }

    private fun ensureSpace(space: Int) {
        val requiredSize = position + space
        var newSize = array.size
        while (newSize < requiredSize) {
            newSize *= 2
        }

        if (newSize > array.size) {
            array = array.copyOf(newSize)
        }
    }
}
