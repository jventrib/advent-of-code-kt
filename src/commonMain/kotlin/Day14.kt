import kotlin.math.ceil

val day14 = day<Long>(14) {
    part1(expectedExampleOutput = 1588, expectedOutput = 3306) {
        input.doPart(10)
    }

    part2(expectedExampleOutput = 2188189693529, expectedOutput = 3760312702877) {
        input.doPart(40)
    }
}

private fun List<String>.doPart(step: Int): Long {
    val polymer = first()
    val rules = drop(2)
        .map { line -> line.split(" -> ") }
        .map { Rule(it.first(), it.last().first(), polymer.count(it.first()).toLong()) }

    val finalRules = (1..step).fold(rules) { acc, i ->
        doStep(acc).also { println("after step $i :$it") }
    }

    val totals = finalRules
        .flatMap { r -> r.input.map { it to r.count } } //get elements counts
        .groupBy { it.first }
        .mapValues { e -> ceil(e.value.sumOf { it.second } / 2.0).toLong() }
    return totals.values.maxOf { it } - totals.values.minOf { it }
}

private fun doStep(rules: List<Rule>): List<Rule> {
    val counts = rules
        .filter { it.count > 0 }
        .flatMap { r -> r.insert().windowed(2).map { it to r.count } } //get count of new pairs created after insertion
        .groupBy { it.first }.mapValues { e -> e.value.sumOf { it.second } } //suppress duplicates
    return rules.map { r -> r.copy(count = counts[r.input] ?: 0L) } //update rules with new counts
}

private fun String.count(s: String) = this.windowed(s.length).count { it == s }

private data class Rule(val input: String, private val output: Char, val count: Long) {
    fun insert() = "${input.first()}$output${input.last()}"
}
