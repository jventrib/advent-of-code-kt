val day13 = day<Int>(13) {
    part1(expectedExampleOutput = 17, expectedOutput = 807) {
        val points = getPoints()
        val folds = getFolds()

        val result = getFoldedPoints(points, folds[0])
        result.size
    }

    part2(expectedExampleOutput = 16, expectedOutput = 98) {
        val points = getPoints()
        val folds = getFolds()

        val result = folds.fold(points, ::getFoldedPoints)
        result.draw(result.maxOf { it.x }, result.maxOf { it.y })
        result.size // LGHEGUEJ
    }
}

private fun List<String>.getFolds() = this
    .filter { it.startsWith("fold") }
    .map { it.substringAfter(" ").substringAfter(" ").split("=") }
    .map { it.first() to it.last().toInt() }

private fun List<String>.getPoints() = this
    .filter { it.contains(",") }
    .map { it.split(",") }
    .map { Point(it.first().toInt(), it.last().toInt()) }.toSet()

private fun Iterable<Point>.draw(width: Int, height: Int) {
    (0..height).forEach { y ->
        val line = (0..width).joinToString("") { x ->
            this.firstOrNull { it == Point(x, y) }?.let { "██" } ?: "  " // double char for clarity
        }
        println(line)
    }
}

private fun getFoldedPoints(
    points: Set<Point>,
    foldPoint: Pair<String, Int>
): Set<Point> {
    val foldPos = foldPoint.second
    val axe: (Point) -> Int = if (foldPoint.first == "x") Point::x else Point::y
    val foldedPoints = points
        .filter { axe(it) > foldPos }
        .map {
            if (foldPoint.first == "x") Point(2 * foldPos - axe(it), it.y)
            else Point(it.x, 2 * foldPos - axe(it))
        }
        .toSet()
    return (points + foldedPoints).filter { axe(it) < foldPos }.toSet()
}
