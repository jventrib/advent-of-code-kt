val day15 = day<Int>(15) {
    part1(expectedExampleOutput = 40, expectedOutput = 621) {
        val points = input.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                MPoint(x, y).apply {
                    //TODO scope function
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
        val tileWidth = input.first().length
        val tileHeight = input.size
        val points = (0 until 5).flatMap { yTime ->
            input.mapIndexed { y, line ->
                (0 until 5).flatMap { xTime ->
                    line.mapIndexed { x, c ->
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
    val queue = PriorityQueue<MPoint>(100) //TODO Java interop
    queue.add(points.flatten().first().apply { dist = 0 })

    var cur = MPoint(0, 0)
    val start = getMillis()

    while (!queue.isEmpty()) {
        cur = queue.poll()
        if (cur == MPoint(width - 1, height - 1)) break
        if (cur !in path) {
            path.add(cur)
            cur.neighborsIn(points)
                .forEach { n ->
                    if (cur.dist < Int.MAX_VALUE && cur.dist + n.risk < n.dist) {
                        n.dist = cur.dist + n.risk
                        queue.add(n)
                    }
                }
        }
    }

    val chrono = getMillis() - start
    println("Dijkstra time: $chrono ms")
    return cur.dist
}


data class MPoint(val x: Int, val y: Int) : Comparable<MPoint> { //TODO data class
    var risk: Int = 0
    var dist: Int = 0
    fun neighborsIn(points: List<List<MPoint>>) = listOfNotNull(
        points[y].getOrNull(x - 1),
        points[y].getOrNull(x + 1),
        points.getOrNull(y - 1)?.get(x),
        points.getOrNull(y + 1)?.get(x),
    )

    override fun toString(): String {
        return "MPoint(x=$x, y=$y, risk=$risk, dist=$dist)"
    }

    override fun compareTo(other: MPoint) = compareBy<MPoint> { it.dist }.compare(this, other)
}

