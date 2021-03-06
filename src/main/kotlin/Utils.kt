import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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
    val day = Day<E>(dayNumber, block)
    return day
}


class Day<E>(val dayNumber: Int, val block: Day<E>.() -> Unit) {
    lateinit var input: List<String>
    lateinit var part1: Part<E>
    lateinit var part1Example: Part<E>
    lateinit var part2: Part<E>
    lateinit var part2Example: Part<E>

    fun part1(expectedExampleOutput: E, expectedOutput: E? = null, block: () -> E): Part<E> {
        part1 = Part(dayNumber, false, expectedOutput, block)
        part1Example = Part(dayNumber, true, expectedExampleOutput, block)
        return part1
    }

    fun part2(expectedExampleOutput: E, expectedOutput: E? = null, block: () -> E): Part<E> {
        part2 = Part(dayNumber, false, expectedOutput, block)
        part2Example = Part(dayNumber, true, expectedExampleOutput, block)
        return part2
    }
}

class Part<E>(
    private val dayNumber: Int,
    example: Boolean,
    val expected: E?,
    private val block: () -> E
) {
    val output get() = block()
    override fun toString(): String {
        return "Part(output=$output)"
    }
}

data class Point(val x: Int, val y: Int, val value: Int = 0)

suspend fun <T, R> Iterable<T>.mapParallel(transform: (T) -> R): List<R> = coroutineScope {
    map { async { transform(it) } }.map { it.await() }
}

fun <T, R> Flow<T>.concurrentMap(
    dispatcher: CoroutineDispatcher,
    concurrencyLevel: Int,
    transform: suspend (T) -> R
): Flow<R> {
    return flatMapMerge(concurrencyLevel) { value ->
        flow { emit(transform(value)) }
    }.flowOn(dispatcher)
}