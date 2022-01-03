val day21 = day<Long>(21) {
    part1(expectedExampleOutput = 739785, expectedOutput = 720750) {
        val player1 = first().substringAfter("Player 1 starting position: ").toInt()
        val player2 = last().substringAfter("Player 2 starting position: ").toInt()

        val result = playGame(player1, player2)
        result
    }

    part2(expectedExampleOutput = 444356092776315, expectedOutput = 275067741811212) {
        val player1 = first().substringAfter("Player 1 starting position: ").toInt()
        val player2 = last().substringAfter("Player 2 starting position: ").toInt()
        val wins = playGame2(Round(player1, 0, player2, 0))

        maxOf(wins.p1, wins.p2)
    }
}

private fun Int.wrap100() = (this - 1) % 100 + 1

private fun Int.wrap() = (this - 1) % 10 + 1


private val eachCount =
    (1..3).flatMap { a -> (1..3).flatMap { b -> (1..3).map { c -> a + b + c } } }.groupingBy { it }.eachCount()

private fun playGame(p1: Int, p2: Int): Long {
    var side = 1
    var rolls = 0

    fun rollDice(): Int {
        return side.also {
            side = (it + 1).wrap100()
            rolls++
        }
    }

    var score1 = 0
    var place1: Int = p1
    var score2 = 0
    var place2: Int = p2
    while (true) {
        place1 = (place1 + rollDice()).wrap()
        place1 = (place1 + rollDice()).wrap()
        place1 = (place1 + rollDice()).wrap()
        score1 += place1
        if (score1 >= 1000)
            return (score2 * rolls).toLong()

        place2 = (place2 + rollDice()).wrap()
        place2 = (place2 + rollDice()).wrap()
        place2 = (place2 + rollDice()).wrap()
        score2 += place2
        if (score2 >= 1000)
            return (score1 * rolls).toLong()
    }
}

private fun playGame2(round: Round): Wins = when {
    round.score1 >= 21 -> Wins(1, 0)
    round.score2 >= 21 -> Wins(0, 1)
    else -> eachCount
        .map { playGame2(round.play(it.key)) * it.value }
        .reduce { acc, wins -> acc + wins }
}


private data class Round(
    val place1: Int,
    val score1: Int,
    val place2: Int,
    val score2: Int,
    val player1ToPlay: Boolean = true
) {
    fun play(threeRollsResult: Int) = if (player1ToPlay) {
        val newPlace1 = (place1 + threeRollsResult).wrap()
        copy(place1 = newPlace1, score1 = score1 + newPlace1, player1ToPlay = false)
    } else {
        val newPlace2 = (place2 + threeRollsResult).wrap()
        copy(place2 = newPlace2, score2 = score2 + newPlace2, player1ToPlay = true)
    }
}

private data class Wins(val p1: Long, val p2: Long) {
    operator fun plus(other: Wins): Wins {
        return Wins(this.p1 + other.p1, this.p2 + other.p2)
    }

    operator fun times(count: Int) = Wins(p1 * count, p2 * count)
}
