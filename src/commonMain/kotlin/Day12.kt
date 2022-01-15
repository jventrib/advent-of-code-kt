typealias Path = List<Cave>

val day12 = day<Int>(12) {
    part1(expectedExampleOutput = 10, expectedOutput = 3463) {
        val group: Map<Cave, List<Cave>> = input.getConnections()
        val paths = buildPath(group, listOf(Cave("start")), true).filter { it.contains(Cave("end")) }
        paths.size
    }

    part2(expectedExampleOutput = 36, expectedOutput = 91533) {
        val group: Map<Cave, List<Cave>> = input.getConnections()
        val paths = buildPath(group, listOf(Cave("start")), false).filter { it.contains(Cave("end")) }
        paths.size
    }
}

private fun List<String>.getConnections(): Map<Cave, List<Cave>> {
    val connections = this.map {
        it.split("-")
    }.flatMap { cave ->
        if (cave.none { it == "start" || it == "end" }) listOf(
            Cave(cave.first()) to Cave(cave.last()),
            Cave(cave.last()) to Cave(cave.first())
        )
        else {
            when {
                cave.last() == "start" -> listOf(Cave(cave.last()) to Cave(cave.first()))
                cave.first() == "end" -> listOf(Cave(cave.last()) to Cave(cave.first()))
                else -> listOf(Cave(cave.first()) to Cave(cave.last()))
            }
        }
    }

    return connections.groupBy { it.first }.mapValues { entry -> entry.value.map { it.second } }
}


fun buildPath(group: Map<Cave, List<Cave>>, path: Path, onceForSmall: Boolean): List<Path> {
    if (path.last().s == "end") return listOf(path)
    val newPaths = getTargets(group, path, onceForSmall).map { path + it }
        .filter { p ->
            p
                .filter { it.isLowerCase() }
                .groupingBy { it }
                .eachCount().values
                .reduce { acc, entry -> acc * entry } <= 2
        }
    return newPaths.flatMap { buildPath(group, it, onceForSmall) }
}

private fun getTargets(group: Map<Cave, List<Cave>>, path: Path, onceForSmall: Boolean = true): Path =
    group.getValue(path.last()).filterNot { onceForSmall && it.isLowerCase() && path.contains(it) }

private fun Cave.isLowerCase() = s.lowercase() == s


expect value class Cave(val s: String)