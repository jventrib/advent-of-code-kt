import kotlin.math.absoluteValue

val day7 = day<Int>(7) {
    part1(expectedExampleOutput = 37, expectedOutput = 344535) {
        val crabs = parseLineToIntList()
        (1..crabs.maxOf { it }).map { dest -> crabs.sumOf { (it - dest).absoluteValue } }.minOf { it }
    }

    part2(expectedExampleOutput = 168, expectedOutput = 95581659) {
        val crabs = parseLineToIntList()
        (1..crabs.maxOf { it }).map { dest -> crabs.sumOf { (1..(dest - it).absoluteValue).sum() } }.minOf { it }
    }
}

