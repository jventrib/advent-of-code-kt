val day02 = day<Int>(2) {
    part1(expectedExampleOutput = 150, expectedOutput = 1893605) {
        val ops = input.map { it.split(" ").run { Command(get(0), get(1).toInt()) } }
            .groupingBy { it.type }.fold(0) { acc, e -> acc + e.amount }
        ((ops["down"] ?: 0) - (ops["up"] ?: 0)) * (ops["forward"] ?: 0)
    }

    part2(expectedExampleOutput = 900, expectedOutput = 2120734350) {
        val submarine = input.map { it.split(" ").run { Command(get(0), get(1).toInt()) } }
            .fold(Submarine(0, 0, 0)) { cur, command ->
                when (command.type) {
                    "up" -> cur.copy(aim = cur.aim - command.amount)
                    "down" -> cur.copy(aim = cur.aim + command.amount)
                    "forward" -> cur.copy(pos = cur.pos + command.amount, depth = cur.depth + cur.aim * command.amount)
                    else -> cur
                }
            }
        submarine.pos * submarine.depth
    }
}

private data class Submarine(val aim: Int, val pos: Int, val depth: Int)
private data class Command(val type: String, val amount: Int)
