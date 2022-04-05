val day06 = day<Long>(6) {
    part1(expectedExampleOutput = 5934, expectedOutput = 374927) {
        input.doPart1(80)
    }

    part2(expectedExampleOutput = 26984457539, expectedOutput = 1687617803407) {
        input.doPart2(256)
    }
}

private fun List<String>.doPart1(days: Int): Long {
    val fish = first().split(",").map(String::toInt)

    val total = (1..days).fold(fish) { acc, _ ->
        acc.map { if (it == 0) 6 else (it - 1) } + acc.filter { it == 0 }.map { 8 } //TODO plus operator
    }
    return total.size.toLong()
}


private fun List<String>.doPart2(days: Int): Long {
    val inputCounts = first().split(",").map(String::toInt).groupingBy { it }.eachCount()
    val initialCount = (0..8).map { inputCounts[it]?.toLong() ?: 0L } //TODO safe call & elvis operator
    return (1..days).fold(initialCount) { count, _ ->
        (count.drop(1) + count.take(1)).mapIndexed { i, e -> if (i == 6) e + count.first() else e } //rotate right
    }.sum()
}


private fun List<String>.doPart2b(days: Int): Long {
    val inputCounts = first().split(",").map(String::toInt).groupingBy { it }.eachCount().withDefault { 0 }
    val initialCount = (0..8).map { inputCounts.getValue(it).toLong() } //TODO withDefault and getValue()
    return (1..days).fold(initialCount) { count, _ ->
        (count.drop(1) + count.take(1)).mapIndexed { i, e -> if (i == 6) e + count.first() else e }
    }.sum()
}



