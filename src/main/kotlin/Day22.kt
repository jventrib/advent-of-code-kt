val day22 = day<Int>(22) {
    part1(expectedExampleOutput = 590784, expectedOutput = 0) {
        val steps = this.map { line ->
            val status = line.substringBefore(" ").let { it == "on" }
            val xRange = line.substringAfter("x=").substringBefore(",").parseRange()
            val yRange = line.substringAfter("y=").substringBefore(",").parseRange()
            val zRange = line.substringAfter("z=").parseRange()
            Step(status, xRange, yRange, zRange)
        }


        val r = steps
            .filter {
                it.xRange.first in -50..50
                it.xRange.last in -50..50
                it.yRange.first in -50..50
                it.yRange.last in -50..50
                it.zRange.first in -50..50
                it.zRange.last in -50..50
            }
            .asSequence().map { Cuboid.from(it) }.reduce { acc, cur -> acc + cur }

        r.cubes.size
    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        0
    }
}

private fun String.parseRange() = split("..").let { it.first().toInt()..it.last().toInt() }


data class Cube(val x: Int, val y: Int, val z: Int, val status: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cube

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }
}


data class Cuboid(val cubes: Set<Cube>) {
    operator fun plus(other: Cuboid): Cuboid {
        return Cuboid((cubes - other.cubes + other.cubes).filter { it.status }.toSet())
    }

    companion object {
        fun from(step: Step): Cuboid {
            val cubes = step.xRange.flatMap { x ->
                step.yRange.flatMap { y ->
                    step.zRange.map { z ->
                        Cube(x, y, z, step.status)
                    }
                }
            }.toSet()
            return Cuboid(cubes)
        }
    }
}

data class Step(val status: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)