import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

val day19 = day<Int>(19) {
    part1(expectedExampleOutput = 79, expectedOutput = 392) {
        val allScanners = input.getScanners()
        val allBeacons = allScanners.flatMap { it.fromScanner0Beacons }.distinct().sorted()
        allBeacons.size
    }

    part2(expectedExampleOutput = 3621, expectedOutput = 13332) {
        val allScanners = input.getScanners()
        val maxDist = allScanners
            .flatMap { sa ->
                allScanners.filter { sb -> sa != sb }
                    .map { sb -> sa.transformation!!.translation to sb.transformation!!.translation }
            }
            .map { abs(it.second.x - it.first.x) + abs(it.second.y - it.first.y) + abs(it.second.z - it.first.z) }
            .maxOf { it }
        maxDist


    }
}

private fun List<String>.getScanners(): List<Scanner> {
    val allScanners = this.fold(listOf<Scanner>()) { acc, s ->
        when {
            s.startsWith("---") -> {
                val number = s.substringAfter("scanner ").substringBefore(" ---").toInt()
                acc + Scanner(
                    number,
                    acc.firstOrNull(),
                    listOf()
                )
            }
            s.isNotEmpty() -> {
                val coord = s.split(",").map { it.toInt() }
                val last =
                    acc.last().copy(localBeacons = acc.last().localBeacons + Point3d(coord[0], coord[1], coord[2]))
                acc.dropLast(1) + last
            }
            else -> acc
        }
    }

    allScanners.first().apply {
        transformation = if (number == 0) Transformation(Matrix3.identity(), Point3d(0, 0, 0)) else null
        fromScanner0Beacons = localBeacons
    }
    allScanners[0].findOverlappingScanners(allScanners)
    return allScanners
}

val orientations = Matrix3.identity().let {
    listOf(
        it,
        it.rotX(90),
        it.rotX(180),
        it.rotX(-90),
        it.rotY(90),
        it.rotY(180),
        it.rotY(-90),
        it.rotZ(90),
        it.rotZ(180),
        it.rotZ(-90),
        it.rotX(90).rotY(90),
        it.rotX(90).rotY(180),
        it.rotX(90).rotY(-90),
        it.rotX(90).rotZ(90),
        it.rotX(90).rotZ(180),
        it.rotX(90).rotZ(-90),
        it.rotX(180).rotY(90),
        it.rotX(180).rotY(-90),
        it.rotX(180).rotZ(90),
        it.rotX(180).rotZ(-90),
        it.rotX(-90).rotY(90),
        it.rotX(-90).rotY(-90),
        it.rotX(-90).rotZ(90),
        it.rotX(-90).rotZ(-90),
    )
}

data class Point3d(val x: Int, val y: Int, val z: Int) : Comparable<Point3d> {
    operator fun plus(other: Point3d) = Point3d(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Point3d) = Point3d(x - other.x, y - other.y, z - other.z)

    override fun toString() = "[$x, $y, $z]"

    override fun compareTo(other: Point3d) =
        compareBy<Point3d> { it.x }.thenBy { it.y }.thenBy { it.z }.compare(this, other)
}

data class Scanner(
    val number: Int,
    val scanner0: Scanner?,
    val localBeacons: List<Point3d>,
) {
    var fromScanner0Beacons: List<Point3d> = listOf()
    var transformation: Transformation? = null

    fun findOverlappingScanners(allScanners: List<Scanner>) {
        if (allScanners.all { it.transformation != null }) return
        val scs = allScanners
            .filter { it.number != this.number }
            .filter { it.transformation == null }
            .mapNotNull { it.checkWith(this) }
        if (scs.isEmpty() || allScanners.all { it.transformation != null }) return
        return scs.forEach { it.findOverlappingScanners(allScanners) }
    }

    private fun checkWith(other: Scanner): Scanner? {
        println("Checking Scanner $this with $other")
        val scannerTransformation = runBlocking {
            orientations.asFlow()
                .concurrentMap(Dispatchers.Default, DEFAULT_CONCURRENCY) { m ->
                    m to getScannerPositionForOrientation(m, other)
                }
                .firstOrNull { it.second != null }
                ?.let { Transformation(it.first, it.second!!) }
        } ?: return null
        val correctedTransformation = other.transformation?.let {
            Transformation(
                it.matrix * scannerTransformation.matrix,
                it.matrix * scannerTransformation.translation + it.translation
            )
        } ?: scannerTransformation

        val fromScanner0Beacons = localBeacons
            .asSequence()
            .map { correctedTransformation.transform(it) }
            .toList()

        return this.apply {
            this.fromScanner0Beacons = fromScanner0Beacons
            this.transformation = correctedTransformation
        }
    }

    private fun getScannerPositionForOrientation(m: Matrix3, other: Scanner): Point3d? {
        val orientedPoints = localBeacons.asSequence().map { m * it }

        return other.localBeacons
            .asSequence()
            .flatMap { p0 -> orientedPoints.map { p0 - it } }
            .firstOrNull { d -> orientedPoints.count { o -> other.localBeacons.contains(o + d) } >= 12 }
    }

    override fun toString(): String {
        return "Scanner(number=$number, pos=$transformation, localBeacons=$localBeacons, fromScanner0Beacons=$fromScanner0Beacons)"
    }
}

data class Transformation(val matrix: Matrix3, val translation: Point3d) {
    fun transform(point3d: Point3d) = matrix * point3d + translation
}

data class Matrix3(
    val m11: Int,
    val m12: Int,
    val m13: Int,
    val m21: Int,
    val m22: Int,
    val m23: Int,
    val m31: Int,
    val m32: Int,
    val m33: Int
) {
    operator fun times(p: Point3d): Point3d {
        val x = m11 * p.x + m12 * p.y + m13 * p.z
        val y = m21 * p.x + m22 * p.y + m23 * p.z
        val z = m31 * p.x + m32 * p.y + m33 * p.z
        return Point3d(x, y, z)
    }

    operator fun times(o: Matrix3): Matrix3 {
        val r11 = m11 * o.m11 + m12 * o.m21 + m13 * o.m31
        val r12 = m11 * o.m12 + m12 * o.m22 + m13 * o.m32
        val r13 = m11 * o.m13 + m12 * o.m23 + m13 * o.m33

        val r21 = m21 * o.m11 + m22 * o.m21 + m23 * o.m31
        val r22 = m21 * o.m12 + m22 * o.m22 + m23 * o.m32
        val r23 = m21 * o.m13 + m22 * o.m23 + m23 * o.m33

        val r31 = m31 * o.m11 + m32 * o.m21 + m33 * o.m31
        val r32 = m31 * o.m12 + m32 * o.m22 + m33 * o.m32
        val r33 = m31 * o.m13 + m32 * o.m23 + m33 * o.m33

        return Matrix3(
            r11, r12, r13,
            r21, r22, r23,
            r31, r32, r33
        )
    }

    fun rotX(d: Int) = this * Matrix3(
        1, 0, 0,
        0, cosI(d), -sinI(d),
        0, sinI(d), cosI(d)
    )

    fun rotY(d: Int) = this * Matrix3(
        cosI(d), 0, sinI(d),
        0, 1, 0,
        -sinI(d), 0, cosI(d)
    )

    fun rotZ(d: Int) = this * Matrix3(
        cosI(d), -sinI(d), 0,
        sinI(d), cosI(d), 0,
        0, 0, 1
    )

    private fun Int.toRadian(): Double = (this.toDouble() / 180 * PI)
    private fun cosI(deg: Int) = cos(deg.toRadian()).toInt()
    private fun sinI(deg: Int) = sin(deg.toRadian()).toInt()
    override fun toString(): String {
        fun Int.d() = this.toString().padStart(2, ' ')
        return """
            |[${m11.d()}, ${m12.d()}, ${m13.d()}]
            |[${m21.d()}, ${m22.d()}, ${m23.d()}]
            |[${m31.d()}, ${m32.d()}, ${m33.d()}]
            | """.trimMargin()
    }

    companion object {
        fun identity() = Matrix3(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        )
    }
}

