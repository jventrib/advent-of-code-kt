val day3 = day<Int>(3) {
    part1(198) {
        val charList = this.flatMap { word ->
            word.toCharArray().mapIndexed { i, c -> Pair(i, c.digitToInt(2)) }.toList()
        }
        val rates = charList
            .groupBy { (pos, _) -> pos }
            .map { (_, chars) ->
                val countGroup = chars.map { it.second }.groupingBy { it }.eachCount()
                countGroup.entries.maxByOrNull { it.value }!!.key to countGroup.entries.minByOrNull { it.value }!!.key
            }
            .fold(Pair<List<Int>, List<Int>>(listOf(), listOf())) { acc, e ->
                acc.copy(acc.first + e.first, acc.second + e.second)
            }
        val gamma = rates.first.joinToString("").toInt(2)
        val epsilon = rates.second.joinToString("").toInt(2)
        gamma * epsilon
    }

    part2(230) {
        val oxygen = keepCommon(this, 0) { getMostCommon(it) }.toInt(2)
        val co2 = keepCommon(this, 0) { getLeastCommon(it) }.toInt(2)
        oxygen * co2
    }
}

private fun keepCommon(list: List<String>, pos: Int, selector: (List<Int>) -> Int): String {
    val charsAtPos = list.map { it[pos].digitToInt(2) }
    val filtered = list.filter { it[pos].digitToInt(2) == selector(charsAtPos) }
    if (filtered.size == 1) return filtered.first()
    return keepCommon(filtered, pos + 1, selector)
}

private fun getMostCommon(charsAtPos: List<Int>) = getCommon(charsAtPos, 1, Map<Int, Int>::maxByOrNull)
private fun getLeastCommon(charsAtPos: List<Int>) = getCommon(charsAtPos, 0, Map<Int, Int>::minByOrNull)


private fun getCommon(
    list: List<Int>,
    onEquals: Int,
    selector: Map<Int, Int>.((Map.Entry<Int, Int>) -> Int) -> Map.Entry<Int, Int>?
): Int {
    val countGroup = list.groupingBy { it }.eachCount()
    if (countGroup[0] == countGroup[1]) return onEquals
    return countGroup.selector { it.value }!!.key
}



