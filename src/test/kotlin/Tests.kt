import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Tests {

    @Test
    fun todayPart1Example() {
        val d = day6
        val p = "Day ${d.dayNumber} - Part1Example"

        println(p)
        if (d.part1Example.expected != null)
            Assertions.assertEquals(d.part1Example.expected, d.part1Example.output)
        else
            d.part1Example.output
    }


    @TestFactory
    fun oneDayTest() = dayTest(day6)

//    @Suppress("UselessCallOnCollection")
//    @TestFactory
//    fun allTests() = days
//        .filterNotNull()
//        .flatMap { d -> dayTest(d) }

    private fun dayTest(d: Day<Long>): List<DynamicTest> {
        val p1e = "Day ${d.dayNumber} - Part1Example"
        val p1 = "Day ${d.dayNumber} - Part1"
        val p2e = "Day ${d.dayNumber} - Part2Example"
        val p2 = "Day ${d.dayNumber} - Part2"
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