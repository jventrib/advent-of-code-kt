import java.util.*
import kotlin.math.abs

fun main() {
    day23.part2.output
}

val day23 = day<Int>(23) {
    part1(expectedExampleOutput = 12521, expectedOutput = 13558) {
        solve(this)
    }


    part2(expectedExampleOutput = 44169, expectedOutput = 56982) {
        val part2Lines = listOf(
            "  #D#C#B#A#",
            "  #D#B#A#C#"
        )
        val list = this.take(3) + part2Lines + this.drop(3)

        solve(list)
    }
}

private fun solve(list: List<String>): Int {
    val initial = Step.fromInput(list)
    initial.total = 0
    val queue = PriorityQueue<Step>()
    val solutions = mutableListOf<Step>()
    queue.add(initial)
    var i = 0
    val totals = mutableMapOf<Set<Amphipod>, Int>().withDefault { Int.MAX_VALUE }
    while (queue.isNotEmpty()) {
        val current = queue.poll()

        val nextSteps = current.amphipods
            .filterNot { it.done }
            .flatMap { it.getNextSteps(current) }
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
    return solution?.total ?: 0
}


data class Step(val amphipods: Set<Amphipod>, private var prev: Step?, var energy: Int) : Comparable<Step> {

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


    val amphisWithWorld: List<CharArray>

    var total = Int.MAX_VALUE

    init {
        val ca = mask
            .flatMapIndexed { index, s ->
                if (index == 3) {
                    (1 until (amphipods.size / 4)).map { s }
                } else listOf(s)
            }
            .map { it.toCharArray() }
        amphipods.map { ca[it.pos.y][it.pos.x] = it.type.symbol }
        amphisWithWorld = ca
    }

    fun getCharAt(x: Int, y: Int) = amphisWithWorld[y.coerceIn(0..7)][x.coerceIn(0..12)]

    fun isDone() = amphipods.all { it.done }

    override fun toString(): String {
        return amphisWithWorld.joinToString(
            separator = System.lineSeparator(),
            postfix = System.lineSeparator(),
            prefix = "Total: $total" + System.lineSeparator()
                    + "Energy: $energy" + System.lineSeparator()
                    + "Amphis Done: ${amphipods.count()}" + System.lineSeparator()
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
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Step
//
//        if (amphipods != other.amphipods) return false
//        if (energy != other.energy) return false
//        if (total != other.total) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = amphipods.hashCode()
//        result = 31 * result + energy
//        result = 31 * result + total
//        return result
//    }

    companion object {
        fun fromInput(input: List<String>): Step {
            fun getAmphipods(): Set<Amphipod> {
                val line = input.flatMapIndexed { y: Int, s: String ->
                    ('A'..'D')
                        .flatMap { c -> Regex(c.toString()).findAll(s).map { c to it.range.first }.toList() }
                        .map {
                            val type = AmphipodType.valueOf(it.first.toString())
                            val x = it.second
                            Amphipod(type, Pos(x, y), done = x == type.xPos && y == input.size - 2)
                        }
                }
                return line.toSet()
            }

            val amphipods = getAmphipods()
            return Step(amphipods = amphipods, null, 0)
        }
    }
}

data class Amphipod(val type: AmphipodType, val pos: Pos, val energy: Int = 0, val done: Boolean = false) {

    private lateinit var step: Step
    fun getNextSteps(step: Step): Set<Step> {
        this.step = step
        val set = buildSet {

            if (canLeaveRoom()) {
                getFreeHallwaySlots(-1 downTo -11).forEach { addStepWithNewPos(pos.x + it, 1) }
                getFreeHallwaySlots(1..11).forEach { addStepWithNewPos(pos.x + it, 1) }
            }

            if (inHallway()) {
                getFreeRooms(1..11).forEach { addStepWithNewPos(pos.x + it, getDepth(pos.x + it), true) }
                getFreeRooms(-1 downTo -11).forEach { addStepWithNewPos(pos.x + it, getDepth(pos.x + it), true) }
            }
        }
        set.filter { it.amphipods.size < 16 }.map {
            println("Missing: $it")
        }
        return set
    }

    private fun getFreeRooms(intRange: IntProgression) = intRange
        .takeWhile { (getChar(pos.x + it, 1) == '.') }
        .filter { getChar(pos.x + it, 2) == '.' }
        .filter { x ->
            (2..getRoomBottom()).all { y ->
                getChar(pos.x + x, y).let { it == type.symbol || it == '.' }
            }
        }.filter { pos.x + it == type.xPos }

    private fun canLeaveRoom() = pos.y > 1 && (1 until pos.y).all { getChar(pos.x, it) == '.' }

    private fun inHallway() = pos.y == 1

    private fun getFreeHallwaySlots(range: IntProgression) = range
        .takeWhile { (getChar(pos.x + it, 1) == '.') }
        .filter { getChar(pos.x + it, 2) == '#' }

    private fun getRoomBottom() = step.amphisWithWorld.size - 2

    private fun getDepth(x: Int) = (2..getRoomBottom())
        .takeWhile { (getChar(x, it) == '.') }
        .maxOf { it }

    private fun MutableSet<Step>.addStepWithNewPos(x: Int, y: Int, done: Boolean = false) {
        val amphipods = newPos(this@Amphipod, x, y, if (done) true else this@Amphipod.done)
        if (amphipods.size < 16) {
            println("Missing: $amphipods")
        }

        add(step.copy(amphipods = amphipods, prev = step, energy = amphipods.sumOf { it.energy }))
    }

    private fun newPos(amphipod: Amphipod, x: Int, y: Int, done: Boolean): Set<Amphipod> {
        val deltaX = x - pos.x
        val deltaY = y - pos.y
        val list = (step.amphipods - amphipod).map { it.copy(energy = 0) } + amphipod.copy(
            pos = pos.copy(x = x, y = y),
            energy = (abs(deltaX) + abs(deltaY)) * type.energyPerStep,
            done = done
        )
        if (list.groupingBy { it.pos }.eachCount().any { it.value > 1 }) {
            println("Missing:")
        }
        val set = list.toSet()

        if (set.size < 16) {
            println("Missing: $set")
        }
        return set
    }

    private fun getChar(x: Int, y: Int) = step.getCharAt(x, y)

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Amphipod
//
//        if (type != other.type) return false
//        if (pos != other.pos) return false
//        if (energy != other.energy) return false
//        if (done != other.done) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = type.hashCode()
//        result = 31 * result + pos.hashCode()
//        result = 31 * result + energy
//        result = 31 * result + done.hashCode()
//        return result
//    }
}

data class Pos(val x: Int, val y: Int)

enum class AmphipodType(val energyPerStep: Int, val xPos: Int) {
    A(1, 3),
    B(10, 5),
    C(100, 7),
    D(1000, 9);

    val symbol get() = name.first()
}
