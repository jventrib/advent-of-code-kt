package d1

import part

const val d = 1

fun day1Part1(fileName: String = "input.txt") = part(d, fileName) {
    part1(this)
}

fun day1Part2(fileName: String = "input.txt") = part(d, fileName) {
    part2(this)
}

private fun part1(list: List<String>) = list.map { it.toInt() }.zipWithNext().count { it.first < it.second }


private fun part2(list: List<String>): Int {
    val tmp = list.map { it.toInt() }.zipWithNext().zipWithNext { a, b -> a.first + a.second + b.second }
    return tmp.zipWithNext().count { it.first < it.second }
}

