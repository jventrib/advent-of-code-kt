import kotlin.math.abs
import kotlin.math.absoluteValue

val day07 = day<Int>(7) {
    part1(expectedExampleOutput = 37, expectedOutput = 344535) {
        val crabs = parseLineToIntList()
        (1..crabs.maxOf { it }).map { dest -> crabs.sumOf { abs(it - dest) } }.minOf { it }
    }

    part2(expectedExampleOutput = 168, expectedOutput = 95581659) {
        val crabs = parseLineToIntList()
        (1..crabs.maxOf { it }).map { dest -> crabs.sumOf { abs(dest - it).let { i -> (i * i + i) / 2 } } }.minOf { it }
    }
}

