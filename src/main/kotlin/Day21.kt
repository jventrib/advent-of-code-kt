val day21 = day<Int>(21) {
    part1(expectedExampleOutput = 739785, expectedOutput = 0) {

        val players = getPlayers()

        var i = 0
        var rolls = 0
        while (true) {
            for (it in players) {
                val num = i * 6 + it.offset
                val space = wrap(it.place, num)
                rolls += 3
                it.place = space
                it.score += space
                if (players.maxOf { it.score >= 1000 }) {
                    println("stop")
                    break
                }
            }
            if (players.maxOf { it.score >= 1000 }) {
                println("stop")
                break
            }
            i++
        }


        players.minOf { it.score } * rolls
    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        val p1 = playGame(Round(1, 4, 0, 8, 0))

        0

    }
}

private fun List<String>.getPlayers(): List<Player> {
    val player1 = first().substringAfter("Player 1 starting position: ").toInt()
    val player2 = last().substringAfter("Player 2 starting position: ").toInt()

    val players = listOf(
        Player(1, player1),
        Player(2, player2)
    )
    return players
}

data class Player(val player: Int, var place: Int, var score: Int = 0) {
    val offset get() = (player - 1) * 3 + 1

}

fun getDiceResult(firstNumber: Int): Int {
    return (firstNumber..firstNumber + 2).sumOf { (it - 1) % 100 + 1 }
}

fun wrap(currentSpace: Int, firstNumber: Int): Int {
    val diceResult = getDiceResult(firstNumber)
    return (currentSpace + diceResult).wrap()
}


fun Int.wrap() = (this - 1) % 10 + 1

data class Round(val player: Int, var place: Int, var score: Int = 0, var place2: Int, var score2: Int = 0) {
    operator fun times(count: Int): Round {
        return this.copy(place = this.place * count, score = this.score * count)
    }

    operator fun plus(other: Round): Round {
        return this.copy(place = this.place + other.place, score = this.score + other.score)
    }

}


val diracDiceSums = (1..3).flatMap { a -> (1..3).flatMap { b -> (1..3).map { c -> a + b + c } } }

data class Acc(val numbers: Map<Int, Long>, val scores: Map<Int, Long>)

val cache = mutableMapOf<Round, Wins>()

fun playGame(round: Round): Wins {
    if (round.score >= 21) return Wins(1, 0)
    if (round.score2 >= 21) return Wins(0, 1)

    val cached = cache[round]
    if (cached != null) return cached
    val r1 = diracDiceSums
        .map { (it + round.place).wrap() }
        .groupingBy { it }.eachCount()
        .map { (place, count) ->
            playGame(round.copy(place = place, score = round.score + place)) * count
        }
    val r2 = diracDiceSums
        .map { (it + round.place2).wrap() }
        .groupingBy { it }.eachCount()
        .map { (place2, count) ->
            playGame(round.copy(place2 = place2, score2 = round.score2 + place2)) * count
        }
    val res = r1.zip(r2) { t1, t2 ->
        t1 + t2
    }.reduce { total: Wins, round: Wins ->
        total + round
    }
    cache[round] = res
    return res
}


data class Wins(val p1: Long, val p2: Long) {
    operator fun plus(other: Wins): Wins {
        return Wins(this.p1 + other.p1, this.p2 + other.p2)
    }

    operator fun times(count: Int) = Wins(p1 * count, p2 * count)
}