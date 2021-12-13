typealias Path = List<Cave>

val day12 = day<Int>(12) {
    part1(expectedExampleOutput = 10, expectedOutput = 3463) {
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


        val group: Map<Cave, List<Cave>> = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val paths = buildPath(group, listOf(listOf(Cave("start"))), true).filter { it.contains(Cave("end")) }
//        println("paths: $paths")
        paths.size
    }

    part2(expectedExampleOutput = 36, expectedOutput = 0) {
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
        val group: Map<Cave, List<Cave>> = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val paths = buildPath(group, listOf(listOf(Cave("start"))), false).filter { it.contains(Cave("end")) }
//        println("paths: $paths")
        paths.size
    }
}


fun buildPath(group: Map<Cave, List<Cave>>, paths: List<Path>, onceForSmall: Boolean): List<Path> {
    val newPaths = paths.asSequence().flatMap { path ->
        val targets = getTargets(group, path, onceForSmall)
        targets?.map { path + it } ?: paths
    }
        .filter { path ->
            val eachCount = path.filter { it.isLowerCase() }.groupingBy { it }.eachCount()
            eachCount.values.reduce { acc, entry -> acc * entry } <= 2
        }
        .distinct().toList()
    println(paths.size)
    if (newPaths == paths) return paths
    return buildPath(group, newPaths, onceForSmall)
}

private fun getTargets(group: Map<Cave, List<Cave>>, path: Path, onceForSmall: Boolean = true) =
    group[path.last()]?.filterNot { onceForSmall && it.isLowerCase() && path.contains(it) }

private fun Cave.isLowerCase() = s.lowercase() == s


@JvmInline
value class Cave(val s: String) {
    override fun toString(): String {
        return s
    }
}