import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestDay1 {
    private val day = day1

    @Test
    fun part1_example() {
        Assertions.assertEquals(7, day.part1Example.output)
    }

    @Test
    fun part1() {
        day.part1.output
    }

    @Test
    fun part2_example() {
        Assertions.assertEquals(5, day.part2Example.output)
    }

    @Test
    fun part2() {
        day.part2.output
    }
}
