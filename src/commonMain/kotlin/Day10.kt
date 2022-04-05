val day10 = day<Long>(10) {
    part1(expectedExampleOutput = 26397, expectedOutput = 265527) {
        val corruptedLines = input.fold(listOf<Char>()) { acc, line ->
            val corrupted = getCorruptedChar(line)
            if (corrupted != null) acc + corrupted else acc
        }
        corruptedLines.mapNotNull { score[it] }.sumOf { it }
    }

    part2(expectedExampleOutput = 288957, expectedOutput = 3969823589) {
        val notCorruptedLines = input.fold(listOf<String>()) { acc, line ->
            val corrupted = getCorruptedChar(line)
            if (corrupted == null) acc + line else acc
        }

        val completionScores = notCorruptedLines.map { line ->
            val stack = ArrayDeque<Char>()
            line.toCharArray()
                .forEach { if (it in setOf('(', '{', '[', '<')) stack.addFirst(it) else stack.removeFirst() }
            val completion = stack.map { closing[it] }
            completion.fold(0L) { acc, c -> acc * 5 + score2[c]!! }
        }
        completionScores.sorted()[completionScores.size / 2]
    }
}

private fun getCorruptedChar(line: String): Char? {
    val stack = ArrayDeque<Char>()
    val corrupted = line.toCharArray().firstOrNull { c ->
        if (c in setOf('(', '{', '[', '<')) {
            stack.addFirst(c)
            false
        } else {
            val openingChar = stack.removeFirst()
            closing[openingChar] != c
        }
    }
    return corrupted
}

val closing = mapOf(
    '(' to ')',
    '{' to '}',
    '[' to ']',
    '<' to '>',
)

val score = mapOf(
    ')' to 3L,
    ']' to 57L,
    '}' to 1197L,
    '>' to 25137L,
)

val score2 = mapOf(
    ')' to 1L,
    ']' to 2L,
    '}' to 3L,
    '>' to 4L,
)
