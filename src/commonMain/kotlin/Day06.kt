val day06 = day<Long>(6) {
    part1(expectedExampleOutput = 5934, expectedOutput = 374927) {
        input.doPart(80)
    }

    part2(expectedExampleOutput = 26984457539, expectedOutput = 1687617803407) {
        input.doPart(256)
    }
}

private fun List<String>.doPart(days: Int): Long {
    val inputCounts = first().split(",").map(String::toInt).groupingBy { it }.eachCount()
    val initialCount = (0..8).map { inputCounts[it]?.toLong() ?: 0L }
    return (1..days).fold(initialCount) { count, _ ->
        (count.drop(1) + count.take(1)).mapIndexed { i, e -> if (i == 6) e + count.first() else e }
    }.sum()
}

