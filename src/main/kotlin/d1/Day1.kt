package d1

import day

val day1 = day<Int>(1) {
    part1 {
        this.map { it.toInt() }.zipWithNext().count { it.first < it.second }
    }

    part2 {
        val tmp = this.map { it.toInt() }.zipWithNext().zipWithNext { a, b -> a.first + a.second + b.second }
        tmp.zipWithNext().count { it.first < it.second }
    }
}
