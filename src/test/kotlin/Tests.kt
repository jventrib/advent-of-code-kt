import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Tests {

    @TestFactory
    fun oneDayTest() = dayTest(day1)

    @Suppress("UselessCallOnCollection")
    @TestFactory
    fun allTests() = days
        .filterNotNull()
        .flatMap { d -> dayTest(d) }

    private fun dayTest(d: Day<Int>): List<DynamicTest> {
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
}