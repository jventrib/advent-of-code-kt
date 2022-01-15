val day17 = day<Int>(17) {
    part1(expectedExampleOutput = 45, expectedOutput = 25200) {
        val hits = input.fireProbes()
        val winner = hits.maxByOrNull { shot -> shot.trajectory.maxOf { it.pos.y } }
//        draw(winner!!, target)
        val maxY = winner!!.trajectory.maxOf { it.pos.y }
        println("Winner -> maxY:$maxY, trajectory: ${winner.trajectory.first().velocity}")
        maxY
    }

    part2(expectedExampleOutput = 112, expectedOutput = 3012) {
        val hits = input.fireProbes()
        hits.count()
    }
}

private fun List<String>.fireProbes(): List<Shot> {
    val xRange = first().substringAfter("x=").substringBefore(",").split("..")
        .run { first().toInt()..last().toInt() }
    val yRange = first().substringAfter("y=").split("..")
        .run { first().toInt()..last().toInt() }
    val target = Rect(xRange, yRange)
    val hits = (0..target.x.last).flatMap { x ->
        (target.y.first..-target.y.first).map { y ->
            tryVelocity(Coord(x, y), target)
        }
    }.filter { it.hit }
    return hits
}

private fun tryVelocity(initialVelocity: Coord, target: Rect): Shot {
    val probe = Probe(Coord(0, 0), initialVelocity)
    val probePositions = mutableListOf<Probe>()

    var cur = probe
    probePositions.add(cur)
    while (cur.pos !in target && cur.pos.y > target.y.minOf { it } && cur.pos.x < target.x.maxOf { it }) {
        cur = cur.copy(pos = cur.pos + cur.velocity, velocity = Coord(handleSpeedX(cur), cur.velocity.y - 1))
        probePositions.add(cur)
    }

    val hit = probePositions
        .any { target.contains(it.pos) }
//    if (hit)
//        draw(probePositions, target)
    return Shot(probePositions, hit)
}

private fun handleSpeedX(acc: Probe) =
    when {
        acc.velocity.x > 0 -> acc.velocity.x - 1
        acc.velocity.x < 0 -> acc.velocity.x + 1
        else -> acc.velocity.x
    }

@Suppress("unused")
private fun draw(shots: List<Shot>, target: Rect) {
    println()
//    val trajectory = shot.trajectory
//    println("Initial velocity: ${trajectory.first().velocity}")

    val height = target.y.minOf { it }
    val top = shots.maxOf { s -> s.trajectory.maxOf { it.pos.y } }
    (top downTo height).forEach { y ->
        (0..target.x.last).forEach { x ->
            when {
                x == 0 && y == 0 -> print('S')
                shots.any { s -> s.trajectory.any { it.pos.x == x && it.pos.y == y } } -> print('#')
                x in target.x && y in target.y -> print('T')
                else -> print('.')
            }
        }
        println()
    }
    println()
}

private data class Rect(val x: IntRange, val y: IntRange) {
    operator fun contains(coord: Coord?) =
        coord != null && coord.x in x && coord.y in y
}

private data class Coord(val x: Int, val y: Int) {
    operator fun plus(other: Coord) = Coord(this.x + other.x, this.y + other.y)
}

private data class Probe(val pos: Coord, val velocity: Coord)
private data class Shot(val trajectory: List<Probe>, val hit: Boolean)

