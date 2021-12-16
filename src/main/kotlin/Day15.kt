val day15 = day<Int>(15) {
    part1(expectedExampleOutput = 40, expectedOutput = 621) {
        val points = mapIndexed { y, line ->
            line.toCharArray().mapIndexed { x, c -> MPoint(x, y).apply { dist = c.digitToInt() } }
        }
        val height = points.size
        val width = points.maxOf { it.size }

        doPart(points.flatten(), width, height)
    }

    part2(expectedExampleOutput = 315, expectedOutput = 0) {
        val points = (0 until 5).flatMap { yTime ->
            mapIndexed { y, line ->
                (0 until 5).flatMap { xTime ->
                    line.toCharArray().mapIndexed { x, c ->
                        MPoint(x + 100 * xTime, y + 100 * yTime).apply {
                            dist = (c.digitToInt() + yTime + xTime).run { if (this > 9) this - 9 else this }
                        }
                    }
                }
            }
        }
        val height = points.size
        val width = points.maxOf { it.size }

        doPart(points.flatten(), width, height)
    }
}

private fun doPart(
    points: List<MPoint>,
    width: Int,
    height: Int
): Int {
    val S = mutableSetOf<MPoint>()
    var Q = mutableListOf(points[0].apply { dist = 0 })

    // Iterations
    var u: MPoint? = null
    while (Q.isNotEmpty()) {
        u = extractMin(Q)
        if (u == MPoint(width - 1, height - 1)) break
        if (u !in S) {
            S.add(u)
            val list = u.neighborsIn(points)
            list
                .forEach { n ->
                    Q.add(n.copy().apply { dist = u.dist + n.dist })
                    Q = Q.distinct().toMutableList()
                }
//            println(S.size)
        }
    }

    return u!!.dist
}


fun extractMin(Q: MutableList<MPoint>): MPoint {
    val node = Q.minByOrNull { it.dist } ?: Q.first()
    Q.remove(node)
    return node
}

data class Edge(val start: MPoint, val end: MPoint, val weight: Int)

data class MPoint(val x: Int, val y: Int) {

    var dist: Int = 0
    fun neighborsIn(points: List<MPoint>): List<MPoint> {
        return listOfNotNull(
            points.firstOrNull { this.x == it.x + 1 && this.y == it.y },
            points.firstOrNull { this.x == it.x - 1 && this.y == it.y },
            points.firstOrNull { this.x == it.x && this.y == it.y - 1 },
            points.firstOrNull { this.x == it.x && this.y == it.y + 1 },
        )
    }

    override fun toString(): String {
        return "MPoint(x=$x, y=$y, dist=$dist)"
    }


}
