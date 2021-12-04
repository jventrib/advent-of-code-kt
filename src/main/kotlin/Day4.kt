import java.util.regex.Pattern

val day4 = day<Int>(4) {
    part1(4512) {
        //init numbers
        val numbers = this.first().split(",").map(String::toInt)
        numbers.size

        //init boards
        val boards = this.drop(2)
            .filterNot { it == "" }
            .chunked(5)
            .map { board ->
                Board(board.flatMap { cell ->
                    cell.trim().split(Pattern.compile(" +")).map { Pair(it.toInt(), false) }
                }.toMap().toMutableMap())
            }

        check(boards.all { it.cases.size == 25 })

        //run the game
        val (number, winner) = numbers.firstNotNullOf { number ->
            boards.forEach { b -> if (b.cases[number] != null) b.cases[number] = true } //Mark each board
            boards.firstOrNull(Board::hasWin)?.let { number to it } //Look for a winner
        }
        check(boards.all { it.cases.size == 25 })

        //calculate winner score
        val unmarked = winner.cases.filterNot { it.value }.entries.sumOf { it.key }
        unmarked * number
    }

    part2(230) {
        0
    }
}

data class Board(val cases: MutableMap<Int, Boolean>) {
    fun hasWin() = hasWinningRow() || hasWinningCol()
    private fun hasWinningRow() = cases.entries.chunked(5).any { row -> row.all { it.value } }
    private fun hasWinningCol() = (0 until 5).any { cases.entries.chunked(5).all { row -> row[it].value } }
}
