package d1

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

class Day1Test {



    @Test
    @Order(1)
    fun part1_example() {
        Assertions.assertEquals(7, day1.part1Example.output)
    }

    @Test
    @Order(2)
    fun part1() {
        day1.part1.output
    }

    @Test
    @Order(3)
    fun part2_example() {
        Assertions.assertEquals(5, day1.part2Example.output)
    }

    @Test
    @Order(4)
    fun part2() {
        day1.part2.output
    }
}
