val day14 = day<Long>(14) {
    part1(expectedExampleOutput = 1588, expectedOutput = 3306) {
        val polymer = first()
        val rules = getRules()


        val result = (1..10).fold(polymer) { acc, i ->
            doStep(rules, acc).also { println("after step $i :$it") }
        }

        getResult(result)
    }

    part2(expectedExampleOutput = 2188189693529, expectedOutput = 0) {
        val polymer = first()
        val rules = getRules()


        val result = (1..40).fold(polymer) { acc, i ->
            doStep(rules, acc).also { println("after step $i") }
        }

        getResult(result)
    }
}

private fun getResult(result: String): Long {
    val charGroups = result.toCharArray().groupBy { it }.mapValues { it.value.size.toLong() }
    return charGroups.values.maxOf { it } - charGroups.values.minOf { it }
}

private fun List<String>.getRules() = this.drop(2).map { line ->
    line.split(" -> ").let { it.first() to it.last() }
}


private fun doStep(rules: List<Pair<String, String>>, polymer: String): String {

    return rules.fold(polymer) { acc, e ->
        var tmp = acc
        while (tmp.contains(e.first)) {
            tmp = tmp.substringBefore(e.first) + e.insert() + tmp.substringAfter(e.first)
        }
        tmp
    }.uppercase()

}

private fun Pair<String, String>.insert() = first.first() + second.lowercase() + first.last()