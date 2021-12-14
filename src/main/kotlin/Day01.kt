val day01 = day<Int>(1) {
    part1(7) {
        this.map { it.toInt() }.zipWithNext().count { it.first < it.second }
    }

    part2(5) {
        val tmp = this.map { it.toInt() }.zipWithNext().zipWithNext { a, b -> a.first + a.second + b.second }
        tmp.zipWithNext().count { it.first < it.second }
    }
}
