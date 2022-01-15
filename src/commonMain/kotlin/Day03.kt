val day03 = day<Int>(3) {
    part1(expectedExampleOutput = 198, expectedOutput = 2498354) {
        val range = 0 until input.maxOf { it.length }
        val joinToString = range.map { pos -> getMostCommon(input.getCharsAtPos(pos)) }.joinToString("")
        val gamma = joinToString.toInt(2)
        val epsilon = range.map { pos -> getLeastCommon(input.getCharsAtPos(pos)) }.joinToString("").toInt(2)
        gamma * epsilon
    }

    part2(expectedExampleOutput = 230, expectedOutput = 3277956) {
        val oxygen = input.keepCommon(0) { getMostCommon(it) }.toInt(2)
        val co2 = input.keepCommon(0) { getLeastCommon(it) }.toInt(2)
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



