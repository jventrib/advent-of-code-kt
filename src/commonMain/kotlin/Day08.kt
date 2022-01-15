
typealias SegmentSet = Set<Segment>

val day08 = day<Int>(8) {
    part1(expectedExampleOutput = 26, expectedOutput = 255) {
        val entries = input.map { l ->
            l.split("|").let { Entry(it.first().trim().split(" "), it.last().trim().split(" ")) }
        }

        val searchedLengths = listOf(2, 3, 4, 7)
        val outputLengthCounts = entries.flatMap { it.output }.groupingBy { it.length }.eachCount()
        outputLengthCounts.filterKeys { searchedLengths.contains(it) }.entries.sumOf { it.value }

    }

    part2(expectedExampleOutput = 61229, expectedOutput = 982158) {
        val entries = input.map { l ->
            l.split("|").let { Entry(it.first().trim().split(" "), it.last().trim().split(" ")) }
        }

        val linesSums = entries.map { entry ->
            val segmentsMapping = getSegments(entry)
            entry.output
                .map(String::enumSet)
                .map { outputNumber ->
                    segmentsMapping.indexOfFirst { outputNumber.size == it.size && outputNumber.containsAll(it) }
                }
        }.map {
            it.joinToString("", transform = Int::toString).toInt()
        }
        linesSums.sum()
    }
}

private fun getSegments(entry: Entry): List<SegmentSet> {
    val signalsByLength: Map<Int, List<SegmentSet>> =
        entry.signals.groupBy { it.length }.mapValues { it.value.map(String::enumSet) }
    val s = MutableList<SegmentSet>(10) { setOf() }
    s[1] = signalsByLength[2]!!.first()
    s[4] = signalsByLength[4]!!.first()
    s[7] = signalsByLength[3]!!.first()
    s[8] = signalsByLength[7]!!.first()

    s[9] = signalsByLength[6].filterContains(s[4]).first()
    s[0] = signalsByLength[6].filterContains(s[1]).filterNotContains(s[4]).first()
    s[6] = signalsByLength[6].filterNotContains(s[1]).first()

    s[3] = signalsByLength[5].filterContains(s[1]).first()
    s[5] = signalsByLength[5].filterNotContains(s[1]).first { c -> s[6].containsAll(c) }
    s[2] = signalsByLength[5].filterNotContains(s[3]).filterNotContains(s[5]).first()
    return s
}

private fun String.enumSet() = map { Segment.valueOf(it.uppercase()) }.toSet()

private fun List<SegmentSet>?.filterContains(s1: SegmentSet?): List<SegmentSet> =
    this!!.filter { c -> c.containsAll(s1!!) }

private fun List<SegmentSet>?.filterNotContains(s1: SegmentSet?): List<SegmentSet> =
    this!!.filterNot { c -> c.containsAll(s1!!) }

data class Entry(val signals: List<String>, val output: List<String>)
enum class Segment { A, B, C, D, E, F, G }

