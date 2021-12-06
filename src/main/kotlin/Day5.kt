typealias Point = Int

const val WIDTH = 1000
val day5 = day<Int>(5) {
    part1(expectedExampleOutput = 5, expectedOutput = 5084) {
        doPart(withDiag = false)
    }

    part2(expectedExampleOutput = 12, expectedOutput = 17882) {
        doPart(withDiag = true)
    }
}

private fun List<String>.doPart(withDiag: Boolean): Int {
    //parse line
    val lines = this.map { l ->
        l
            .split("->")
            .map(String::trim)
            .map { it.split(",") }
            .map { (x, y) -> index(x.toInt(), y.toInt()) }
    }.map { Line(it.first(), it.last()) }

    val allLines = lines.map { l ->
        getLinePoints(l, withDiag)
    }
    return allLines
        .flatten()
        .groupBy { it }
        .count { it.value.size > 1 }
}

private fun index(x: Point, y: Point) = y * WIDTH + x
private val Point.x get() = this % WIDTH
private val Point.y get() = this / WIDTH
private fun getYRange(line: Line) = if (line.a.y > line.b.y) line.a.y downTo line.b.y else line.a.y..line.b.y
private fun getXRange(line: Line) = if (line.a.x > line.b.x) line.a.x downTo line.b.x else line.a.x..line.b.x

fun getLinePoints(line: Line, withDiag: Boolean): List<Point> {
    return when {
        withDiag && line.a.x != line.b.x && line.a.y != line.b.y -> {
            val yRange = getYRange(line)
            getXRange(line).mapIndexed { index, it -> index(it, yRange.elementAt(index)) }
        }
        line.a.y == line.b.y -> {
            getXRange(line).map { index(it, line.a.y) }
        }
        line.a.x == line.b.x -> {
            getYRange(line).map { index(line.a.x, it) }
        }
        else -> listOf()
    }
}

data class Line(val a: Point, val b: Point, val count: Int = 0) {
    override fun toString(): String {
        return "Line(start=[${a.x},${a.y}], end=[${b.x},${b.y}], count=$count)"
    }
}


