import kotlin.math.max
import kotlin.math.min

val day16 = day<List<Long>>(16) {
    part1(expectedExampleOutput = listOf(14, 8, 15, 11, 13, 19, 16, 20), expectedOutput = listOf(821)) {
        input.map {
            val packet = getPacket(it.bin())
            println(packet.versionSum)
            packet.versionSum.toLong()
        }
    }

    part2(expectedExampleOutput = listOf(3, 54, 7, 9, 1, 0, 0, 1), expectedOutput = listOf(2056021084691)) {
        input.map {
            val packet = getPacket(it.bin())
            val value = packet.value
            println(value)
            value!!
        }
    }
}

private fun getPacket(input: String): Packet {
    val header = readHeader(input)
    val packet = when (header.type) {
        4 -> Packet.Literal(header, input)
        else -> Packet.Operator(header, input)
    }
    packet.parse()
    return packet
}

private fun readHeader(input: String): Header {
    val version = input.substring(0, 3).toInt(2)
    val type = input.substring(3, 6).toInt(2)
    return Header(version, type)
}

data class Header(val version: Int, val type: Int)

sealed class Packet(val header: Header, val input: String) {
    protected var length = 0
    var value: Long? = null
    var versionSum: Int = header.version

    abstract fun parse()

    override fun toString(): String {
        return "Packet(header=$header, length=$length, value=$value, input='$input')"
    }

    class Literal(header: Header, input: String) : Packet(header, input) {
        private val inputPadded = input.padEnd(24, '0')
        private val bSegment = inputPadded.substring(16, 17).toInt()

        override fun parse() {
            var groupBit = 1
            var groupLiteral = ""
            var groupsLength = 0
            var groups = inputPadded.removeRange(0, 6)
            while (groupBit == 1) {
                groupBit = groups[0].digitToInt()
                groupLiteral += groups.substring(1, 5)
                groupsLength += 5
                groups = groups.removeRange(0, 5)
            }

            value = groupLiteral.toLong(2)
            length = 6 + groupsLength
            println("literal: $value")
        }
    }

    class Operator(header: Header, input: String) : Packet(header, input) {

        override fun parse() {
            val i = input.substring(6, 7)

            if (i == "0") { // length
                val l = input.substring(7, 22).toInt(2)
                println("operator type length: $l")
                var parsed = 22
                while (parsed < l + 22) {
                    parsed = parseSubPacket(parsed)
                }
                length = parsed
            } else { // number
                val l = input.substring(7, 18).toInt(2)
                println("operator type nb: $l")
                var parsed = 18
                repeat(l) {
                    parsed = parseSubPacket(parsed)
                }
                length = parsed
            }
        }

        private fun parseSubPacket(parsed: Int): Int {
            val child = getPacket(input.substring(parsed))
            versionSum += child.versionSum
            operate(child)
            return parsed + child.length
        }

        private fun operate(a: Packet) {
            when (header.type) {
                0 -> value = value?.plus(a.value ?: 0) ?: a.value// sum
                1 -> value = value?.times(a.value ?: value ?: 1) ?: a.value // times
                2 -> value = min(value ?: a.value ?: 0, a.value ?: value ?: 0) //min
                3 -> value = max(value ?: a.value ?: Long.MAX_VALUE, a.value ?: value ?: Long.MAX_VALUE) //min
                4 -> value = a.value // literal
                5 -> value = value?.let { if (value!! > a.value!!) 1 else 0 } ?: a.value //greater
                6 -> value = value?.let { if (value!! < a.value!!) 1 else 0 } ?: a.value //greater
                7 -> value = value?.let { if (value!! == a.value!!) 1 else 0 } ?: a.value//greater
            }
        }

    }
}

private fun String.bin() = this.toCharArray().joinToString("") { hexToBin.getValue(it) }

val hexToBin = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)