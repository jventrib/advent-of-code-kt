val day2 = day<Int>(2) {
    part1 {
        val operations = this.map { it.split(" ").run { get(0) to get(1).toInt() } }
            .groupingBy { it.first }.fold(0) { acc, e -> acc + e.second }
        ((operations["down"] ?: 0) - (operations["up"] ?: 0)) * (operations["forward"] ?: 0)
    }

    part2 {
        val submarine = this.map { it.split(" ").run { Command(get(0), get(1).toInt()) } }
            .fold(Submarine(0, 0, 0)) { cur, command ->
                when (command.type) {
                    "up" -> Submarine(cur.aim - command.amount, cur.pos, cur.depth)
                    "down" -> Submarine(cur.aim + command.amount, cur.pos, cur.depth)
                    "forward" -> Submarine(cur.aim, cur.pos + command.amount, cur.depth + cur.aim * command.amount)
                    else -> cur
                }
            }
        submarine.pos * submarine.depth
    }
}

private data class Submarine(val aim: Int, val pos: Int, val depth: Int)
private data class Command(val type: String, val amount: Int)
