import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Tests {

    val day = day20
    val days = listOf(day01, day02, day03, day04, day05, day06, day07, day08, day09, day10, day11,
        day12, day13, day14, day15, day16, day17, day18, day19, day20)

    @Test
    fun todayPart1Example() {
        dayPartTest(day, { part1Example }, "Part1Example")
    }

    @Test
    fun todayPart1() {
        dayPartTest(day, { part1 }, "Part1")
    }

    @Test
    fun todayPart2Example() {
        dayPartTest(day, { part2Example }, "Part2Example")
    }

    @Test
    fun todayPart2() {
        dayPartTest(day, { part2 }, "Part2")
    }


    private fun <E> dayPartTest(d: Day<E>, part: Day<E>.() -> Part<E>, label: String) {
        val p = "Day ${d.dayNumber} - $label"

        println(p)
        if (d.part().expected != null)
            Assertions.assertEquals(d.part().expected, d.part().output)
        else
            d.part().output
    }


    @TestFactory
    fun oneDayTest() = dayTest(day)

    @Suppress("UselessCallOnCollection")
    @TestFactory
    fun allTests() = days
        .filterNotNull()
        .flatMap { d -> dayTest(d) }

    private fun <E> dayTest(d: Day<E>): List<DynamicTest> {
        val p1e = "Day ${d.dayNumber.pad()} - Part1Example"
        val p1 = "Day ${d.dayNumber.pad()} - Part1"
        val p2e = "Day ${d.dayNumber.pad()} - Part2Example"
        val p2 = "Day ${d.dayNumber.pad()} - Part2"
        return listOf(
            DynamicTest.dynamicTest(p1e) {
                println(p1e)
                Assertions.assertEquals(d.part1Example.expected, d.part1Example.output)
            },
            DynamicTest.dynamicTest(p1) {
                println(p1)
                if (d.part1.expected != null)
                    Assertions.assertEquals(d.part1.expected, d.part1.output)
                else
                    d.part1.output
            },
            DynamicTest.dynamicTest(p2e) {
                println(p2e)
                Assertions.assertEquals(d.part2Example.expected, d.part2Example.output)
            },
            DynamicTest.dynamicTest(p2) {
                println(p2)
                if (d.part2.expected != null)
                    Assertions.assertEquals(d.part2.expected, d.part2.output)
                else
                    d.part2.output
            }
        )
    }

    @Test
    fun casesWinningColumnsTest() {
        val colWin = Board(
            mutableMapOf(
                Pair(1, true), Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false),
                Pair(6, true), Pair(7, false), Pair(8, false), Pair(9, false), Pair(10, false),
                Pair(11, true), Pair(12, false), Pair(13, false), Pair(14, false), Pair(15, false),
                Pair(16, true), Pair(17, false), Pair(18, false), Pair(19, false), Pair(20, false),
                Pair(21, true), Pair(22, false), Pair(23, false), Pair(24, false), Pair(25, false),
            )
        ).hasWin()
        Assertions.assertTrue(colWin)

        val rowWin = Board(
            mutableMapOf(
                Pair(1, true), Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false),
                Pair(6, true), Pair(7, true), Pair(8, true), Pair(9, true), Pair(10, true),
                Pair(11, true), Pair(12, false), Pair(13, false), Pair(14, false), Pair(15, false),
                Pair(16, true), Pair(17, false), Pair(18, false), Pair(19, false), Pair(20, false),
                Pair(21, false), Pair(22, false), Pair(23, false), Pair(24, false), Pair(25, false),
            )
        ).hasWin()
        Assertions.assertTrue(rowWin)

        val loose = Board(
            mutableMapOf(
                Pair(1, true), Pair(2, false), Pair(3, false), Pair(4, false), Pair(5, false),
                Pair(6, false), Pair(7, true), Pair(8, false), Pair(9, false), Pair(10, false),
                Pair(11, true), Pair(12, false), Pair(13, false), Pair(14, false), Pair(15, false),
                Pair(16, true), Pair(17, false), Pair(18, false), Pair(19, false), Pair(20, false),
                Pair(21, true), Pair(22, false), Pair(23, false), Pair(24, false), Pair(25, false),
            )
        ).hasWin()

        Assertions.assertFalse(loose)
    }

}

private fun Int.pad() = this.toString().padStart(2, '0')
