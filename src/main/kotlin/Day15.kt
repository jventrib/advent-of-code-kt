import java.util.*

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

        doPart(points, width, height)
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

        doPart(points, width, height)
    }
}

private fun doPart(
    points: List<List<MPoint>>,
    width: Int,
    height: Int
): Int {
    val path = mutableSetOf<MPoint>()
    val queue = PriorityQueue<MPoint>()
    queue.addAll(points.flatten())
    queue.first().dist = 0

    println("computing neighbors...")
    val neighbors = points.map { lines -> lines.map { it.neighborsIn(points) } }
    println("neighbors computed")

    // Iterations
    var cur: MPoint? = null
    while (queue.isNotEmpty()) {
        cur = queue.poll()
        if (cur == MPoint(width - 1, height - 1)) break
        if (cur !in path) {
            path.add(cur)
            val list = neighbors[cur.y][cur.x]
            list
                .forEach { n ->
                    if (cur.dist < Int.MAX_VALUE && n.dist > cur.dist + n.risk) {
                        n.dist = cur.dist + n.risk
                        queue.remove(n)
                        queue.add(n)
                    }
                }
        }
        println("Q:" + queue.size)
    }

    return cur!!.dist
}


data class MPoint(val x: Int, val y: Int) : Comparable<MPoint> {
    var risk: Int = 0
    var dist: Int = 0
    fun neighborsIn(points: List<List<MPoint>>): List<MPoint> {

        val lines = points.filter { this.y == it.first().y - 1 || this.y == it.first().y || this.y == it.first().y + 1 }
            .flatten()
        return listOfNotNull(
            lines.firstOrNull { this.x == it.x + 1 && this.y == it.y },
            lines.firstOrNull { this.x == it.x - 1 && this.y == it.y },
            lines.firstOrNull { this.x == it.x && this.y == it.y - 1 },
            lines.firstOrNull { this.x == it.x && this.y == it.y + 1 },
        )
    }

    override fun toString(): String {
        return "MPoint(x=$x, y=$y, risk=$risk, dist=$dist)"
    }

    override fun compareTo(other: MPoint) = compareBy<MPoint> { it.dist }.compare(this, other)


}

