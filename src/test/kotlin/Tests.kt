import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Tests {

    val day = day24
    val days = listOf(
        day01, day02, day03, day04, day05, day06, day07, day08, day09, day10, day11,
        day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25
    )

    @Test
    fun todayPart1Example() {
        doPart1Example(day)
    }

    @Test
    fun todayPart1() {
        doPart1(day)
    }

    @Test
    fun todayPart2Example() {
        doPart2Example(day)
    }

    @Test
    fun todayPart2() {
        doPart2(day)
    }

    private fun <E> doPart1Example(d: Day<E>) {
        dayPartTest(d, true, { part1Example }, "Part1Example")
    }

    private fun <E> doPart1(d: Day<E>) {
        dayPartTest(d, false, { part1 }, "Part1")
    }

    private fun <E> doPart2Example(d: Day<E>) {
        dayPartTest(d, true, { part2Example }, "Part2Example")
    }

    private fun <E> doPart2(d: Day<E>) {
        dayPartTest(d, false, { part2 }, "Part2")
    }


    private fun <E> dayPartTest(d: Day<E>, example: Boolean, part: Day<E>.() -> Part<E>, label: String) {
        println("Day ${d.dayNumber} - $label")

        d.input = readInput(d.dayNumber, if (example) "input_example.txt" else "input.txt")
        println("input: ${d.input}")
        val start = System.currentTimeMillis()
        d.block(d)
        val output = d.part().output
        val elapsed = System.currentTimeMillis() - start
        println("output: $output")
        println("time: ${elapsed}ms")

        if (d.part().expected != null)
            Assertions.assertEquals(d.part().expected, output)
        else
            output
    }

    @TestFactory
    fun oneDayTest() = dayTest(day)

    @Suppress("UselessCallOnCollection")
    @TestFactory
    fun allTests() = days
        .filterNotNull()
        .flatMap { d -> dayTest(d) }

    @Suppress("UselessCallOnCollection")
    @TestFactory
    fun allTestsWithoutExample() = days
        .filterNotNull()
        .flatMap { d -> dayTest(d, false) }


    private fun <E> dayTest(d: Day<E>, withExample: Boolean = true): List<DynamicTest> {
        val p1e = "Day ${d.dayNumber.pad()} - Part1Example"
        val p1 = "Day ${d.dayNumber.pad()} - Part1"
        val p2e = "Day ${d.dayNumber.pad()} - Part2Example"
        val p2 = "Day ${d.dayNumber.pad()} - Part2"
        return buildList {
            if (withExample) add(DynamicTest.dynamicTest(p1e) {
                doPart1Example(d)
            })
            add(DynamicTest.dynamicTest(p1) {
                doPart1(d)
            })
            if (withExample) add(DynamicTest.dynamicTest(p2e) {
                doPart2Example(d)
            })
            add(DynamicTest.dynamicTest(p2) {
                doPart2(d)
            })
        }
    }
}

private fun Int.pad() = this.toString().padStart(2, '0')

