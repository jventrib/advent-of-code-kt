val day6 = day<Long>(6) {
    part1(expectedExampleOutput = 5934, expectedOutput = 374927) {
        doPart(80)
    }

    part2(expectedExampleOutput = 26984457539, expectedOutput = 1687617803407) {
        doPart(256)
    }
}

private fun List<String>.doPart(days: Int): Long {
    val inputCounts = first().split(",").map(String::toInt).groupingBy { it }.eachCount()
    val initialCount = (0..8).map { inputCounts[it]?.toLong() ?: 0L }

    return (1..days).fold(initialCount) { count, _ ->
        count.windowed(size = 2, partialWindows = true).mapIndexed { index, window ->
            when (index) {
                6 -> window.last() + count[0]
                8 -> count[0]
                else -> window.last()
            }
        }
    }.sum()
}

