val day01 = day<Int>(1) {
    part1(expectedExampleOutput = 7, expectedOutput = 1154) {
        input.map { it.toInt() }.zipWithNext().count { it.first < it.second }
    }

    part2(expectedExampleOutput = 5, expectedOutput = 1127) {
        val tmp = input.map { it.toInt() }.zipWithNext().zipWithNext { a, b -> a.first + a.second + b.second }
        tmp.zipWithNext().count { it.first < it.second }
    }
}
