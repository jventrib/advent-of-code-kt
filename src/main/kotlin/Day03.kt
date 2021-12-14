val day03 = day<Int>(3) {
    part1(198) {
        val range = 0 until 5
        val gamma = range.map { pos ->
            getMostCommon(getCharsAtPos(pos))
        }.joinToString("").toInt(2)

        val epsilon = range.map { pos ->
            getLeastCommon(getCharsAtPos(pos))
        }.joinToString("").toInt(2)
        gamma * epsilon
    }

    part2(230) {
        val oxygen = this.keepCommon(0) { getMostCommon(it) }.toInt(2)
        val co2 = this.keepCommon(0) { getLeastCommon(it) }.toInt(2)
        oxygen * co2
    }
}

private fun List<String>.getCharsAtPos(pos: Int) = this.map { it[pos].digitToInt(2) }

private fun List<String>.keepCommon(pos: Int, selector: (List<Int>) -> Int): String {
    val filtered = filter { it[pos].digitToInt(2) == selector(getCharsAtPos(pos)) }
    return if (filtered.size == 1) filtered.first() else filtered.keepCommon(pos + 1, selector)
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



