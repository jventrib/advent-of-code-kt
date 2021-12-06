val day6 = day<Long>(6) {
    part1(expectedExampleOutput = 5934, expectedOutput = 374927) {
        doPart(80)
    }

    part2(expectedExampleOutput = 26984457539, expectedOutput = 17882) {
        val fishs = this.first().split(",").map(String::toByte)
        0
    }
}

private fun List<String>.doPart(days: Int): Long {
    val fishs = this.first().split(",").map(String::toByte)

    val total = (1..days).fold(fishs) { acc, _ ->
        (acc.map { if (it == (0.toByte())) 6 else (it - 1).toByte() } + acc.filter { it == (0.toByte()) }.map { 8 }).also {
//                println("day $i: ${it.joinToString(",")}")
        }
    }
    return total.size.toLong()
}

