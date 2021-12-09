import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis
import kotlin.time.Duration

typealias IOFun<E> = List<String>.() -> E

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String = "input.txt") = File("src/main/resources/d${day}/$name").readLines()


fun List<String>.parseLineToIntList() = first().split(",").map(String::toInt)

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)


fun <R> doPart(day: Int, fileName: String = "input.txt", part: List<String>.() -> R): R {
    val input = readInput(day, fileName)
    println("input: $input")
    val start = System.currentTimeMillis()
    val output = part(input)
    val elapsed = System.currentTimeMillis() - start
    println("output: $output")
    println("time: ${elapsed}ms")
    return output
}

fun <E> day(dayNumber: Int, block: Day<E>.() -> Unit): Day<E> {
    val day = Day<E>(dayNumber)
    day.block()
    return day
}


class Day<E>(val dayNumber: Int) {
    lateinit var part1: Part<E>
    lateinit var part1Example: Part<E>
    lateinit var part2: Part<E>
    lateinit var part2Example: Part<E>

    fun part1(expectedExampleOutput: E, expectedOutput: E? = null, block: IOFun<E>): Part<E> {
        part1 = Part(dayNumber, false, expectedOutput, block)
        part1Example = Part(dayNumber, true, expectedExampleOutput, block)
        return part1
    }

    fun part2(expectedExampleOutput: E, expectedOutput: E? = null, block: IOFun<E>): Part<E> {
        part2 = Part(dayNumber, false, expectedOutput, block)
        part2Example = Part(dayNumber, true, expectedExampleOutput, block)
        return part2
    }
}

class Part<E>(private val dayNumber: Int, example: Boolean, val expected: E?, private val block: IOFun<E>) {
    private val fileName = if (example) "input_example.txt" else "input.txt"
    val output get() = doPart(dayNumber, fileName, part = block)
    override fun toString(): String {
        return "Part(output=$output)"
    }


}

val days = listOf(day1, day2, day3, day4, day5, day6, day7, day8, day9)