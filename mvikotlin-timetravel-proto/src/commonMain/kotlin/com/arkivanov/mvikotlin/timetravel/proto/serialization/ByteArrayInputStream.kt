package com.arkivanov.mvikotlin.timetravel.proto.serialization

@UseExperimental(ExperimentalUnsignedTypes::class)
internal class ByteArrayInputStream(
    private val array: ByteArray
) {

    private var position = 0

    fun readInt(): Int {
        val b0 = readUByte().toInt()
        val b1 = readUByte().toInt() shl 8
        val b2 = readUByte().toInt() shl 16
        val b3 = readUByte().toInt() shl 24

        return b3 or b2 or b1 or b0
    }

    fun readLong(): Long {
        val b0 = readUByte().toLong()
        val b1 = readUByte().toLong() shl 8
        val b2 = readUByte().toLong() shl 16
        val b3 = readUByte().toLong() shl 24
        val b4 = readUByte().toLong() shl 32
        val b5 = readUByte().toLong() shl 40
        val b6 = readUByte().toLong() shl 48
        val b7 = readUByte().toLong() shl 56

        return b7 or b6 or b5 or b4 or b3 or b2 or b1 or b0
    }

    fun readShort(): Short {
        val b0 = readUByte().toInt()
        val b1 = readUByte().toInt() shl 8

        return (b1 or b0).toShort()
    }

    fun readByte(): Byte = array[position++]

    fun readFloat(): Float = Float.fromBits(readInt())

    fun readDouble(): Double = Double.fromBits(readLong())

    fun readChar(): Char = readShort().toChar()

    fun readBoolean(): Boolean = readByte() != 0.toByte()

    @UseExperimental(ExperimentalStdlibApi::class)
    fun readString(): String? {
        when (readByte()) {
            0.toByte() -> return null
            1.toByte() -> return ""
        }

        val size = readInt()
        val s = array.decodeToString(startIndex = position, endIndex = position + size)
        position += size

        return s
    }

    fun readStringNotNull(): String = requireNotNull(readString())

    private fun readUByte(): UByte = array[position++].toUByte()
}
