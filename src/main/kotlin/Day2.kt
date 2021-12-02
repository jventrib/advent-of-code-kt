val day2 = day<Int>(2) {
    part1 {
        val operations = this.map { it.split(" ").run { get(0) to get(1).toInt() } }
            .groupingBy { it.first }.fold(0) { acc, e -> acc + e.second }
        ((operations["down"] ?: 0) - (operations["up"] ?: 0)) * (operations["forward"] ?: 0)
    }

    part2 {
        val submarine = this.map { it.split(" ").run { get(0) to get(1).toInt() } }
            .fold(Submarine(0, 0, 0)) { acc, pair ->
                when (pair.first) {
                    "up" -> Submarine(acc.aim - pair.second, acc.pos, acc.depth)
                    "down" -> Submarine(acc.aim + pair.second, acc.pos, acc.depth)
                    "forward" -> Submarine(acc.aim, acc.pos + pair.second, acc.depth + acc.aim * pair.second)
                    else -> acc
                }
            }
        submarine.pos * submarine.depth
    }
}

private data class Submarine(val aim: Int, val pos: Int, val depth: Int)
