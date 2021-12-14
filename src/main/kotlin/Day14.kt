import kotlin.math.ceil

val day14 = day<Long>(14) {
    part1(expectedExampleOutput = 1588, expectedOutput = 3306) {
        val polymer = first()
        val rules = getRules()

        val initialCounts = rules.map { Rule(it.first, it.second.first(), polymer.count(it.first)) }

        val result = (1..10).fold(initialCounts) { acc, i ->
            doStep(acc).also { println("after step $i :$it") }
        }

        val charCounts =
            result.map { it.input to it.count }.flatMap { r -> r.first.toCharArray().map { it to r.second } }
        val totals = charCounts.groupBy { it.first }.mapValues { ceil(it.value.sumOf { it.second }/2.0).toLong() }
        totals.values.maxOf { it } - totals.values.minOf { it }

    }

    part2(expectedExampleOutput = 2188189693529, expectedOutput = 0) {
        val polymer = first()
        val rules = getRules()

        val initialCounts = rules.map { Rule(it.first, it.second.first(), polymer.count(it.first)) }

        val result = (1..40).fold(initialCounts) { acc, i ->
            doStep(acc).also { println("after step $i :$it") }
        }

        val charCounts =
            result.map { it.input to it.count }.flatMap { r -> r.first.toCharArray().map { it to r.second } }
        val totals = charCounts.groupBy { it.first }.mapValues { ceil(it.value.sumOf { it.second }/2.0).toLong() }
        totals.values.maxOf { it } - totals.values.minOf { it }
    }
}

private fun getResult(result: String): Long {
    val charGroups = result.toCharArray().groupBy { it }.mapValues { it.value.size.toLong() }
    return charGroups.values.maxOf { it } - charGroups.values.minOf { it }
}

private fun List<String>.getRules() = this.drop(2).map { line ->
    line.split(" -> ").let { it.first() to it.last() }
}


private fun doStep(rules: List<Rule>): List<Rule> {
    val newRules = rules
        .filter { it.count > 0 }
        .flatMap { r -> (0 until r.count).flatMap { r.insert().windowed(2) } }
        .map { o -> rules.getRule(o) }
        .groupingBy { it.input }.eachCount()

    return rules.map { r -> r.copy(count = newRules[r.input] ?: 0) }

//    val newRules = rules.flatMap {
//        val windowed = it.insert().windowed(2)
//        val newChars = windowed.map { r ->
//            val rule = rules.firstOrNull { it.input == r }
//            rule?.copy(count = rule.count + 1)
//        }.filterNotNull()
//        println(newChars)
//        newChars
//    }.distinct()

}

private fun List<Rule>.getRule(input: String) = first { it.input == input }

private fun Rule.insert() = "${input.first()}$output${input.last()}"

private data class Rule(val input: String, val output: Char, val count: Int)

private fun String.count(s: String) = this.windowed(s.length).count { it == s }