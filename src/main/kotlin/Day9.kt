val day9 = day<Int>(9) {
    part1(expectedExampleOutput = 15, expectedOutput = 560) {
        val matrix = this.map { it.toCharArray().toList().map(Char::digitToInt) }
        val width = matrix.maxOf { it.size }
        val height = matrix.size
        val lowPoints = getLowPoints(matrix, width, height)
        lowPoints.sumOf { it.value + 1 }
    }

    part2(expectedExampleOutput = 1134, expectedOutput = 959136) {
        val matrix = this.map { it.toCharArray().toList().map(Char::digitToInt) }
        val width = matrix.maxOf { it.size }
        val height = matrix.size
        val basins = getLowPoints(matrix, width, height).map { lowPoint ->
            fillBasin(Basin(lowPoint, setOf(lowPoint)), matrix, width, height)
        }

        basins.map { it.points.size }.sortedDescending().take(3).reduce { acc, i -> acc * i }
    }
}

private fun fillBasin(basin: Basin, matrix: List<List<Int>>, width: Int, height: Int): Basin {
    val oldSize = basin.points.size
    val newPoints = basin.points.fold(basin.points) { acc, point ->
        val newInBasin = getNeighbors(matrix, width, height, point.x, point.y)
            .filter { n -> n.value != 9 }
        acc + newInBasin
    }
    val newBasin = basin.copy(points = newPoints)
//    println("basin(${newBasin.points.size}): $newBasin")
    return if (newPoints.size != oldSize) fillBasin(newBasin, matrix, width, height) else newBasin
}

private fun getLowPoints(matrix: List<List<Int>>, width: Int, height: Int): List<Point> =
    matrix.foldIndexed(listOf()) { y, acc, cols ->
        acc + cols.foldIndexed(listOf()) { x, colsAcc, cur ->
            val neighbors = getNeighbors(matrix, width, height, x, y)
            val min = neighbors.minOf { it.value }
            if (cur < min) {
                colsAcc + Point(x, y, cur)
            } else colsAcc
        }
    }

private fun getNeighbors(matrix: List<List<Int>>, width: Int, height: Int, x: Int, y: Int): Set<Point> {
    val leftIndex = (x - 1)
    val rightIndex = (x + 1)
    val topIndex = (y - 1)
    val bottomIndex = (y + 1)

    return buildSet {
        if (leftIndex >= 0) add(Point(leftIndex, y, matrix[y][leftIndex]))
        if (rightIndex < width) add(Point(rightIndex, y, matrix[y][rightIndex]))
        if (topIndex >= 0) add(Point(x, topIndex, matrix[topIndex][x]))
        if (bottomIndex < height) add(Point(x, bottomIndex, matrix[bottomIndex][x]))
    }
}

private data class Basin(val lowPoint: Point, val points: Set<Point>)