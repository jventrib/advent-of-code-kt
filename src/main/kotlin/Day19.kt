val day19 = day<Int>(19) {
    part1(expectedExampleOutput = 0, expectedOutput = 0) {

        val s = this.fold(listOf<Scanner>()) { acc, s ->
            when {
                s.startsWith("---") -> acc + Scanner(
                    s.substringAfter("scanner ").substringBefore(" ---").toInt(),
                    listOf()
                )
                s.isNotEmpty() -> {
                    val coord = s.split(",").map { it.toInt() }
                    val last = acc.last().copy(beacons = acc.last().beacons + Point3d(coord[0], coord[1], coord[2]))
                    acc.dropLast(1) + last
                }
                else -> acc
            }
        }


        0
    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        0
    }
}


data class Point3d(val x: Int, val y: Int, val z: Int)

data class Scanner(val number: Int, val beacons: List<Point3d>)