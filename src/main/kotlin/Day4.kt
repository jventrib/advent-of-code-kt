import java.util.regex.Pattern

val day4 = day<Int>(4) {
    part1(4512) {
        val numbers = initNumbers()
        val boards = initBoards()


        //run the game
        val (number, winner) = numbers.firstNotNullOf { number ->
            boards.forEach { b -> b.markCell(number) } //Mark each board
            boards.firstOrNull(Board::hasWin)?.let { number to it } //Look for a winner
        }
        check(boards.all { it.cells.size == 25 })

        //calculate winner score
        val unmarked = winner.cells.filterNot { it.value }.entries.sumOf { it.key }
        unmarked * number
    }

    part2(1924) {
        val numbers = initNumbers()
        val boards = initBoards()

        //run the game
        val winners = numbers.map { number -> //Look for a winner
            boards.forEach { b -> b.markCell(number) } //Mark each board
            boards.filter(Board::hasWin).let { newWinners ->
                newWinners.forEach { it.saveWinningCells(number) }
                number to newWinners
            } //Look for a winner
        }
        val (_, lastWinners) = winners.filterNot { it.second.isEmpty() }.last()

        check(boards.all { it.cells.size == 25 })

        //calculate winner score
        val unmarked = lastWinners.first().winningCells.filterNot { it.value }.entries.sumOf { it.key }
        unmarked * lastWinners.first().winningNumber!!
    }
}

private fun Board.markCell(number: Int) {
    if (cells[number] != null) cells[number] = true
}

private fun List<String>.initBoards(): List<Board> {
    //init boards
    val boards = this.drop(2)
        .filterNot { it == "" }
        .chunked(5)
        .map { board ->
            Board(board.flatMap { cell ->
                cell.trim().split(Pattern.compile(" +")).map { Pair(it.toInt(), false) }
            }.toMap().toMutableMap())
        }

    check(boards.all { it.cells.size == 25 })
    return boards
}

private fun List<String>.initNumbers(): List<Int> {
    //init numbers
    val numbers = this.first().split(",").map(String::toInt)
    numbers.size
    return numbers
}

data class Board(val cells: MutableMap<Int, Boolean>) {
    var winningNumber: Int? = null
    val winningCells: MutableMap<Int, Boolean> = mutableMapOf()
    fun hasWin() = hasWinningRow() || hasWinningCol()
    fun saveWinningCells(number: Int) {
        if (winningNumber == null) {
            winningNumber = number
            winningCells.putAll(cells)
        }
    }

    private fun hasWinningRow() = winningNumber == null && cells.entries.chunked(5).any { row -> row.all { it.value } }
    private fun hasWinningCol() =
        winningNumber == null && (0 until 5).any { cells.entries.chunked(5).all { row -> row[it].value } }

}
