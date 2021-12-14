import kotlin.math.ceil

val day14 = day<Long>(14) {
    part1(expectedExampleOutput = 1588, expectedOutput = 3306) {
        doPart(10)
    }

    part2(expectedExampleOutput = 2188189693529, expectedOutput = 3760312702877) {
        doPart(40)
    }
}

private fun List<String>.doPart(step: Int): Long {
    val polymer = first()
    val rules = drop(2)
        .map { line -> line.split(" -> ").let { it.first() to it.last() } }
        .map { Rule(it.first, it.second.first(), polymer.count(it.first).toLong()) }

    val finalRules = (1..step).fold(rules) { acc, i ->
        doStep(acc).also { println("after step $i :$it") }
    }

    val totals = finalRules
        .map { it.input to it.count }
        .flatMap { r -> r.first.toCharArray().map { it to r.second } }
        .groupBy { it.first }
        .mapValues { e -> ceil(e.value.sumOf { it.second } / 2.0).toLong() }
    return totals.values.maxOf { it } - totals.values.minOf { it }
}

private fun doStep(rules: List<Rule>): List<Rule> {
    val newRules = rules
        .filter { it.count > 0 }
        .flatMap { r -> r.insert().windowed(2).map { it to r.count } }
        .groupBy { it.first }.mapValues { e -> e.value.sumOf { it.second } }
    return rules.map { r -> r.copy(count = newRules[r.input] ?: 0L) }
}

private fun String.count(s: String) = this.windowed(s.length).count { it == s }

private data class Rule(val input: String, private val output: Char, val count: Long) {
    fun insert() = "${input.first()}$output${input.last()}"
}
