import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class Tests {

    @TestFactory
    fun oneDayTest() = dayTest(day1)

    @TestFactory
    fun allTests() = days
        .filterNotNull()
        .flatMap { d -> dayTest(d) }

    private fun dayTest(d: Day<Int>) = listOf(
        DynamicTest.dynamicTest("Day ${d.dayNumber} - Part1Example") {
            Assertions.assertEquals(d.part1Example.expected, d.part1Example.output)
        },
        DynamicTest.dynamicTest("Day ${d.dayNumber} - Part1") {
            if (d.part1.expected != null)
                Assertions.assertEquals(d.part1.expected, d.part1.output)
            else
                d.part1.output
        },
        DynamicTest.dynamicTest("Day ${d.dayNumber} - Part2Example") {
            Assertions.assertEquals(d.part2Example.expected, d.part2Example.output)
        },
        DynamicTest.dynamicTest("Day ${d.dayNumber} - Part2") {
            if (d.part2.expected != null)
                Assertions.assertEquals(d.part2.expected, d.part2.output)
            else
                d.part2.output
        }
    )
}