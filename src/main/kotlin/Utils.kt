import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String="input.txt") = File("src/main/resources/d${day}/$name").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)


fun <R> part(day: Int, fileName: String = "input.txt", part: List<String>.() -> R): R {
    val input = readInput(day, fileName)
    println("input: $input")
    val output = part(input)
    println("output: $output")
    return output
}