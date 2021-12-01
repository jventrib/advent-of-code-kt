package d1

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Day1 {

    @Test
    fun part1_example() {
        Assertions.assertEquals(7, day1Part1("input_example.txt"))
    }

    @Test
    fun part1() {
        day1Part1()
    }

    @Test
    fun part2_example() {
        Assertions.assertEquals(5, day1Part2("input_example.txt"))
    }

    @Test
    fun part2() {
        day1Part2()
    }
}
