import Amphipod.Room.*
import kotlin.math.abs

fun main() {
    day23.part1Example.output
}

val day23 = day<Int>(23) {
    part1(expectedExampleOutput = 0, expectedOutput = 0) {
        val initial = World23.fromInput(this)

        val queue = ArrayDeque<World23>()
        val solutions = mutableSetOf<World23>()
        queue.add(initial)
        var i = 0
        var minEnergy: Int = Int.MAX_VALUE
        while (queue.isNotEmpty()) {

            val w = queue.removeFirst()

//            if (processed.contains(w.hashCode()))
//                continue
//            processed.add(w.hashCode())

            val movements = w.amphipods
                .filterNot { it.done }
                .flatMap { it.getMovements(w) }
                .filter { it.energy <= minEnergy }
                .sortedBy { it.energy }


            if (movements.isNotEmpty() && movements.all { it.isDone() }) {
                solutions.addAll(movements)
                minEnergy = minOf(movements.minOf { it.energy }, minEnergy)
            }

            queue.addAll(0, movements)
            if (i++ % 10000 == 0) {
                val cc = w.amphipods.count { it.done }
                println("i:$i, Queue: ${queue.size}, minEnergy: $minEnergy, energy: ${w.energy}, solutions: ${solutions.size} done: $cc")
            }

//            if (i > 20000000) break
        }

//        val r = w.doStep(w)


//        val solutions = r.filter { it.amphipods.all { it.done } }
        solutions.minByOrNull { it.energy }?.printHistory()
        0
    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        0
    }
}

val mask = listOf(
    "#############",
    "#...........#",
    "###.#.#.#.###",
    "  #.#.#.#.#  ",
    "  #########  "
)

data class World23(val amphipods: Set<Amphipod>, private var prev: World23?, var energy: Int) {

    val amphisWithWorld: List<CharArray>

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
            prefix = "Energy: ${energy}" + System.lineSeparator()
        ) {
            it.joinToString(
                ""
            )
        }
    }

    fun getHistory(): List<String> {
        return prev?.let { it.getHistory() + toString() } ?: listOf()
    }


    fun printHistory() {
        val history = getHistory()
        println("##### History start #####")
        println(history.joinToString(System.lineSeparator(), postfix = System.lineSeparator()))
        println("##### History stop #####")
        println()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as World23

        if (amphipods != other.amphipods) return false
        if (energy != other.energy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amphipods.hashCode()
        result = 31 * result + energy
        return result
    }


    companion object {
        fun fromInput(input: List<String>): World23 {
            fun getAmphipods(y: Int): Set<Amphipod> {
                val line = input.drop(y).first()
                return ('A'..'D')
                    .flatMap { c -> Regex(c.toString()).findAll(line).map { c to it.range.first }.toList() }
                    .map { Amphipod(AmphipodType.valueOf(it.first.toString()), Pos(it.second, y)) }
                    .toSet()
            }
            return World23(amphipods = getAmphipods(2) + getAmphipods(3), null, 0)

        }

    }
}

data class Amphipod(val type: AmphipodType, val pos: Pos, val totalEnergy: Int = 0, val done: Boolean = false) {

    var prevPos: Pos = pos.copy()

    lateinit var world: World23
    fun getMovements(world: World23): Set<World23> {

        this.world = world
        val set = buildSet<World23> {
            //Starting pos ok
//            if (getCharWithOffset(0, 0) == type.name.first() && pos.x == type.xPos && pos.y == 3) {
//                addWorldWithNewPos(world, 0, 0, true)
//            }

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


    private fun canGoToRoom(intRange: IntProgression, room: Room): List<Int> {
        val xCandidates = getFreeHallwayToRoom(intRange, room)
            .filter { pos.x + it == this@Amphipod.type.xPos }
        return xCandidates
    }


    enum class Room {
        Upper, Lower
    }

    private fun MutableSet<World23>.addWorldWithNewPos(
        deltaX: Int,
        deltaY: Int,
        done: Boolean = false
    ) {
        val amphipods = newPos(this@Amphipod, deltaX, deltaY, done)
        add(world.copy(amphipods = amphipods, prev = world, energy = amphipods.sumOf { it.totalEnergy }))
    }

    private fun newPos(
        amphipod: Amphipod,
        deltaX: Int,
        deltaY: Int,
        done: Boolean
    ): Set<Amphipod> = world.amphipods - amphipod + amphipod.copy(
        pos = pos.copy(pos.x + deltaX, pos.y + deltaY),
        totalEnergy = totalEnergy + (abs(deltaX) + abs(deltaY)) * type.energyPerStep,
        done = done
    )

    private fun canLeaveUpperRoom() = pos.y == 2
            && getCharWithOffset(-1, 0) == '#'
            && getCharWithOffset(1, 0) == '#'

    private fun canLeaveLowerRoom() = pos.y == 3
            && getCharWithOffset(-1, 0) == '#'
            && getCharWithOffset(1, 0) == '#'
            && getCharWithOffset(0, -1) == '.'

    private fun inHallway(): Boolean {
        return pos.y == 1
    }


    private fun getChar(x: Int, y: Int) = world.getCharAt(x, y)

    private fun getCharWithOffset(deltaX: Int, deltaY: Int) = getChar(pos.x + deltaX, pos.y + deltaY)

    fun freeLeftHallway(y: Int) = getFreeHallway(-1 downTo -9, y)

    fun freeRightHallway(y: Int) = getFreeHallway(1..9, y)

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


data class Pos(val x: Int, val y: Int) {
}

enum class AmphipodType(val label: String, val energyPerStep: Int, val xPos: Int) {
    A("Amber", 1, 3),
    B("Bronze", 10, 5),
    C("Copper", 100, 7),
    D("Desert", 1000, 9)
}