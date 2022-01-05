import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

typealias IOFun<E> = List<String>.() -> E

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String = "input.txt") =
    File("src/main/resources/d${day.toString().padStart(2, '0')}/$name").readLines()


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

data class Point(val x: Int, val y: Int, val value: Int = 0)


fun <T, R> Iterable<T>.pmap(
    numThreads: Int = Runtime.getRuntime().availableProcessors() - 2,
    exec: ExecutorService = Executors.newFixedThreadPool(numThreads),
    transform: (T) -> R): List<R> {

    // default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
    val defaultSize = if (this is Collection<*>) this.size else 10
    val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))

    for (item in this) {
        exec.submit { destination.add(transform(item)) }
    }

    exec.shutdown()
    exec.awaitTermination(1, TimeUnit.DAYS)

    return ArrayList<R>(destination)
}

suspend fun <T, R> Iterable<T>.mapParallel(transform: (T) -> R): List<R> = coroutineScope {
    map { async { transform(it) } }.map { it.await() }
}