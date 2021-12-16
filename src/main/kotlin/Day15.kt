val day15 = day<Int>(15) {
    part1(expectedExampleOutput = 40, expectedOutput = 621) {
        val points = mapIndexed { y, line ->
            line.toCharArray().mapIndexed { x, c ->
                MPoint(x, y).apply {
                    risk = c.digitToInt()
                    dist = Int.MAX_VALUE
                }
            }
        }
        val height = points.size
        val width = points.maxOf { it.size }

        doPart(points.flatten(), width, height)
    }

    part2(expectedExampleOutput = 315, expectedOutput = 2904) {
        val tileWidth = this.first().length
        val tileHeight = this.size
        val points = (0 until 5).flatMap { yTime ->
            mapIndexed { y, line ->
                (0 until 5).flatMap { xTime ->
                    line.toCharArray().mapIndexed { x, c ->
                        MPoint(x + tileWidth * xTime, y + tileHeight * yTime).apply {
                            risk = (c.digitToInt() + yTime + xTime).run { if (this > 9) this - 9 else this }
                            dist = Int.MAX_VALUE
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
    val path = mutableSetOf<MPoint>()
    val queue = points.apply { first().dist = 0 }.toMutableList()

    println("computing neighbors...")
    val neighbors = points.map { it to it.neighborsIn(points) }.toMap()
    println("neighbors computed")

    // Iterations
    var cur: MPoint? = null
    while (queue.isNotEmpty()) {
        cur = extractMin(queue)
        if (cur == MPoint(width - 1, height - 1)) break
        if (cur !in path) {
            path.add(cur)
            val list = neighbors.getValue(cur)
            list
                .forEach { n ->
                    if (cur.dist < Int.MAX_VALUE && n.dist > cur.dist + n.risk)
                        n.dist = cur.dist + n.risk
                }
        }
        println("Q:" + queue.size)
    }

    return cur!!.dist
}


fun extractMin(queue: MutableList<MPoint>): MPoint {
    val node = queue.minByOrNull { it.dist } ?: queue.first()
    queue.remove(node)
    return node
}

data class Edge(val start: MPoint, val end: MPoint, val weight: Int)

data class MPoint(val x: Int, val y: Int) {

    var risk: Int = 0
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
        return "MPoint(x=$x, y=$y, risk=$risk, dist=$dist)"
    }


}
