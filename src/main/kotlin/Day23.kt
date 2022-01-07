import Amphipod.Room.*
import java.util.*
import kotlin.math.abs

fun main() {
    day23.part1.output
}

val day23 = day<Int>(23) {
    part1(expectedExampleOutput = 12521, expectedOutput = 13558) {
        solve(this)
    }


    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        val part2Lines = """
            #D#C#B#A#
            #D#B#A#C#""".trimIndent().lines()
        val list = this + part2Lines

        solve(list)
    }
}

private fun solve(list: List<String>): Int {
    val initial = Step.fromInput(list)
    initial.total = 0
    val queue = PriorityQueue<Step>()
//    val visited = mutableSetOf<Step>()
    val solutions = mutableListOf<Step>()
    queue.add(initial)
    var i = 0
    val totals = mutableMapOf<Set<Amphipod>, Int>().withDefault { Int.MAX_VALUE }
    while (queue.isNotEmpty()) {
        val current = queue.poll()
//        visited.add(current)

        val nextSteps = current.amphipods
            .filterNot { it.done }
            .flatMap { it.getNextSteps(current) }
//                .filterNot { visited.contains(it) }
//            .sortedBy { it.energy }
//                .filter { w.total < Int.MAX_VALUE && it.total > w.total + it.energy }
//                .onEach { it.total = w.total + it.energy }
//                .filter { it.total in listOf(0, 40, 240) }


        nextSteps.forEach { next ->
            val costForStep = totals.getValue(next.amphipods)
            val newCost = current.total + next.energy
            if (newCost < costForStep) {
                next.total = newCost
                totals[next.amphipods] = newCost
                queue.add(next)
            }
        }

        if (nextSteps.isNotEmpty() && nextSteps.all { it.isDone() }) {
            solutions.addAll(nextSteps)
        }

        if (i++ % 10000 == 0) {
            val cc = current.amphipods.count { it.done }
            println("i:$i, Queue: ${queue.size}, energy: ${current.energy}, solutions: ${solutions.size} done: $cc")
        }
    }
    val solution = solutions.minByOrNull { it.total }
    solution?.printHistory()
    return solution!!.total
}

val mask = listOf(
    "#############",
    "#...........#",
    "###.#.#.#.###",
    "#.#.#.#.#",
    "#########"
).map {
    val s = it.padEnd(11, ' ').padStart(13, ' ')
    s
}

data class Step(val amphipods: Set<Amphipod>, private var prev: Step?, var energy: Int) : Comparable<Step> {

    private val amphisWithWorld: List<CharArray>

    var total = Int.MAX_VALUE

    init {
        val ca = mask.map { it.toCharArray() }
        amphipods.forEach { ca[it.pos.y][it.pos.x] = it.type.name.first() }
        amphisWithWorld = ca
    }

    fun getCharAt(x: Int, y: Int) = amphisWithWorld[y.coerceIn(0..4)][x.coerceIn(0..12)]

    fun isDone() = amphipods.all { it.done }

    override fun toString(): String {

        return amphisWithWorld.joinToString(
            separator = System.lineSeparator(),
            postfix = System.lineSeparator(),
            prefix = "Total: $total" + System.lineSeparator()
                    + "Energy: $energy" + System.lineSeparator()
                    + "Amphis Done: ${amphipods.count { it.done }}" + System.lineSeparator()
        ) {
            it.joinToString(
                ""
            )
        }
    }

    private fun getHistory(): List<Step> {
        return prev?.let { it.getHistory() + this } ?: listOf(this)
    }


    fun printHistory() {
        val history = getHistory()
        println("##### History start #####")
        println(history.joinToString(System.lineSeparator(), postfix = System.lineSeparator()))
        println("##### History stop #####")
        println()
    }


    override fun compareTo(other: Step) = compareBy<Step> { it.energy }.compare(this, other)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Step

        if (amphipods != other.amphipods) return false
        if (energy != other.energy) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amphipods.hashCode()
        result = 31 * result + energy
        result = 31 * result + total
        return result
    }

    companion object {
        fun fromInput(input: List<String>): Step {
            fun getAmphipods(y: Int): Set<Amphipod> {
                val line = input.drop(y).first()
                return ('A'..'D')
                    .flatMap { c -> Regex(c.toString()).findAll(line).map { c to it.range.first }.toList() }
                    .map {
                        val type = AmphipodType.valueOf(it.first.toString())
                        val x = it.second
                        Amphipod(type, Pos(x, y), done = x == type.xPos && y == 3)
                    }
                    .toSet()
            }
            return Step(amphipods = getAmphipods(2) + getAmphipods(3), null, 0)
        }
    }
}

data class Amphipod(val type: AmphipodType, val pos: Pos, val totalEnergy: Int = 0, val done: Boolean = false) {

    private lateinit var world: Step
    fun getNextSteps(world: Step): Set<Step> {

        this.world = world
        val set = buildSet {

            //Starting upper
            if (canLeaveUpperRoom()) {
                // and go Left
                freeLeftHallway(-1).forEach { addWorldWithNewPos(it, -1) }
                //and go Right
                freeRightHallway(-1).forEach { addWorldWithNewPos(it, -1) }
            }
            //Starting lower
            if (canLeaveLowerRoom()) {
                // and go Left
                freeLeftHallway(-2).forEach { addWorldWithNewPos(it, -2) }
                //and go Right
                freeRightHallway(-2).forEach { addWorldWithNewPos(it, -2) }
            }

            if (inHallway()) {
                canGoToRoom(1..9, Lower).forEach {
                    addWorldWithNewPos(it, 2, true)
                }
                canGoToRoom(1..9, Upper).forEach {
                    addWorldWithNewPos(it, 1, true)
                }
                canGoToRoom(-1 downTo -9, Lower).forEach {
                    addWorldWithNewPos(it, 2, true)
                }
                canGoToRoom(-1 downTo -9, Upper).forEach {
                    addWorldWithNewPos(it, 1, true)
                }
            }
        }
        return set
    }

    private fun canGoToRoom(intRange: IntProgression, room: Room) =
        getFreeHallwayToRoom(intRange, room).filter { pos.x + it == type.xPos }

    enum class Room {
        Upper, Lower
    }

    private fun MutableSet<Step>.addWorldWithNewPos(
        deltaX: Int,
        deltaY: Int,
        done: Boolean = false
    ) {
        val amphipods = newPos(this@Amphipod, deltaX, deltaY, if (done) true else this@Amphipod.done)
        add(world.copy(amphipods = amphipods, prev = world, energy = amphipods.sumOf { it.totalEnergy }))
    }

    private fun newPos(
        amphipod: Amphipod,
        deltaX: Int,
        deltaY: Int,
        done: Boolean
    ): Set<Amphipod> = (world.amphipods - amphipod).map { it.copy(totalEnergy = 0) }.toSet() + amphipod.copy(
        pos = pos.copy(x = pos.x + deltaX, y = pos.y + deltaY),
        totalEnergy = (abs(deltaX) + abs(deltaY)) * type.energyPerStep,
        done = done
    )

    private fun canLeaveUpperRoom() = pos.y == 2
            && getCharWithOffset(-1, 0) == '#'
            && getCharWithOffset(1, 0) == '#'

    private fun canLeaveLowerRoom() = pos.y == 3
            && getCharWithOffset(-1, 0) == '#'
            && getCharWithOffset(1, 0) == '#'
            && getCharWithOffset(0, -1) == '.'
            && pos.x != type.xPos

    private fun inHallway(): Boolean {
        return pos.y == 1
    }

    private fun getChar(x: Int, y: Int) = world.getCharAt(x, y)

    private fun getCharWithOffset(deltaX: Int, deltaY: Int) = getChar(pos.x + deltaX, pos.y + deltaY)

    private fun freeLeftHallway(y: Int) = getFreeHallway(-1 downTo -9, y)

    private fun freeRightHallway(y: Int) = getFreeHallway(1..9, y)

    private fun getFreeHallway(range: IntProgression, y: Int) = range
        .takeWhile { (getCharWithOffset(it, y) == '.') }
        .filter { getCharWithOffset(it, y + 1) == '#' }

    private fun getFreeHallwayToRoom(range: IntProgression, room: Room) =
        if (room == Upper) range
            .takeWhile { (getChar(pos.x + it, 1) == '.') }
            .filter { getChar(pos.x + it, 2) == '.' }
            .filter { getChar(pos.x + it, 3) == type.name.first() }
        else range
            .takeWhile { (getChar(pos.x + it, 1) == '.') }
            .filter { getChar(pos.x + it, 2) == '.' }
            .filter { getChar(pos.x + it, 3) == '.' }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Amphipod

        if (type != other.type) return false
        if (pos != other.pos) return false
        if (totalEnergy != other.totalEnergy) return false
        if (done != other.done) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + pos.hashCode()
        result = 31 * result + totalEnergy
        result = 31 * result + done.hashCode()
        return result
    }
}

data class Pos(val x: Int, val y: Int)

enum class AmphipodType(val energyPerStep: Int, val xPos: Int) {
    A(1, 3),
    B(10, 5),
    C(100, 7),
    D(1000, 9)
}
