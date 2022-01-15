import kotlin.math.max
import kotlin.math.min

val day22 = day<Long>(22) {
    part1(expectedExampleOutput = 590784, expectedOutput = 503864) {
        val steps = input.map { line ->
            val status = line.substringBefore(" ").let { it == "on" }
            val xRange = line.substringAfter("x=").substringBefore(",").parseRange()
            val yRange = line.substringAfter("y=").substringBefore(",").parseRange()
            val zRange = line.substringAfter("z=").parseRange()
            Cuboid(xRange, yRange, zRange, status, 0L)
        }

        val r = steps
            .filter {
                it.x.first in -50..50
                it.x.last in -50..50
                it.y.first in -50..50
                it.y.last in -50..50
                it.z.first in -50..50
                it.z.last in -50..50
            }

        val reactor = Reactor(r)
        reactor.reboot()
    }

    part2(expectedExampleOutput = 39769202357779L, expectedOutput = 1255547543528356L) {
        val steps = input.map { line ->
            val status = line.substringBefore(" ").let { it == "on" }
            val xRange = line.substringAfter("x=").substringBefore(",").parseRange()
            val yRange = line.substringAfter("y=").substringBefore(",").parseRange()
            val zRange = line.substringAfter("z=").parseRange()
            Cuboid(xRange, yRange, zRange, status, 0L)
        }

        val reactor = Reactor(steps)
        reactor.reboot()
    }
}


private fun String.parseRange() = split("..").let { it.first().toLong()..it.last().toLong() }


data class Cuboid(val x: LongRange, val y: LongRange, val z: LongRange, val on: Boolean, val realCount: Long) {
    fun onCubes() = x * y * z * if (on) 1 else -1

    infix fun int(other: Cuboid) =
        Cuboid(this.x int other.x, this.y int other.y, this.z int other.z, !this.on, 0L)

    override fun toString(): String {
        return "Cuboid(x=$x, y=$y, z=$z, on=$on, onCubes=${onCubes()}, realOnCubes=$realCount)"
    }
}

infix fun LongRange.int(other: LongRange): LongRange {
    if (this.first > other.last || this.last < other.first) return LongRange.EMPTY // No overlap
    val starts = max(this.first, other.first)
    val ends = min(this.last, other.last)
    return starts..ends
}

private operator fun LongRange.times(other: LongRange): Long {
    return (this.last - this.first + 1L) * other
}

private operator fun Long.times(other: LongRange): Long = this * (other.last - other.first + 1)

data class Reactor(val cuboids: List<Cuboid>) {
    fun reboot() = cuboids.fold<Cuboid, List<Cuboid>>(listOf()) { volumes, cuboid ->
        val intersections = getIntersections(volumes, cuboid)
        if (cuboid.on) volumes + intersections + cuboid else volumes + intersections
    }.sumOf { it.onCubes() }

    private fun getIntersections(cuboids: List<Cuboid>, cuboid: Cuboid) = cuboids
        .filter { (cuboid int it).onCubes() != 0L }
        .map { it int cuboid }
}
