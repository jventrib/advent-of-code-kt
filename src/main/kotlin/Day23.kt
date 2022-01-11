import java.util.*
import kotlin.math.abs

fun main() {
    day23.part2.output
}

val day23 = day<Int>(23) {
    part1(expectedExampleOutput = 12521, expectedOutput = 13558) {
        solve(input)
    }

    part2(expectedExampleOutput = 44169, expectedOutput = 56982) {
        val part2Lines = listOf(
            "  #D#C#B#A#",
            "  #D#B#A#C#"
        )
        val list = input.take(3) + part2Lines + input.drop(3)
        solve(list)
    }
}

private fun solve(list: List<String>): Int {
    val queue = PriorityQueue<Step>()
    val initial = Step.fromInput(list).apply { total = 0 }
    queue.add(initial)
    val solutions = mutableListOf<Step>()
    val totals = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    var i = 0
    while (queue.isNotEmpty()) {
        val nextStates = mutableListOf<Step>()
        val current = queue.poll()
        val nextSteps = current.amphipods
            .filterNot { it.done }
            .flatMap { it.getNextSteps(current) }

        nextSteps.forEach { next ->
            val costForStep = totals.getValue(next.layout)
            val newCost = current.total + next.energy
            if (newCost < costForStep) {
                next.total = newCost
                totals[next.layout] = newCost
                nextStates.add(next)
            }
        }

        queue.addAll(nextStates)
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

    private val mask = listOf(
        "#############",
        "#...........#",
        "###.#.#.#.###",
        "#.#.#.#.#",
        "#########"
    ).map { it.padEnd(11, ' ').padStart(13, ' ') }

    val amphisWithWorld: List<CharArray>

    var total = Int.MAX_VALUE

    val layout: String

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

        layout = amphisWithWorld.joinToString(System.lineSeparator(), transform = ::String)
    }

    fun getCharAt(x: Int, y: Int) = amphisWithWorld[y.coerceIn(0..7)][x.coerceIn(0..12)]

    fun isDone() = amphipods.all { it.done }

    override fun toString(): String {
        return """Total: $total
            |Energy: $energy
            |Amphis Done: ${amphipods.count()}
            |$layout""".trimMargin()
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
                getFreeHallwaySlots(1..11).forEach { addStepWithNewPos(pos.x + it, 1) }
                getFreeHallwaySlots(-1 downTo -11).forEach { addStepWithNewPos(pos.x + it, 1) }
            }

            if (inHallway()) {
                getFreeRooms(1..11).forEach { addStepWithNewPos(pos.x + it, getDepth(pos.x + it), true) }
                getFreeRooms(-1 downTo -11).forEach { addStepWithNewPos(pos.x + it, getDepth(pos.x + it), true) }
            }
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
        add(step.copy(amphipods = amphipods, prev = step, energy = amphipods.sumOf { it.energy }))
    }

    private fun newPos(amphipod: Amphipod, x: Int, y: Int, done: Boolean): Set<Amphipod> {
        val deltaX = x - pos.x
        val deltaY = y - pos.y
        return ((step.amphipods - amphipod).map { it.copy(energy = 0) } + amphipod.copy(
            pos = pos.copy(x = x, y = y),
            energy = (abs(deltaX) + abs(deltaY)) * type.energyPerStep,
            done = done
        )).toSet()
    }

    private fun getChar(x: Int, y: Int) = step.getCharAt(x, y)
}

data class Pos(val x: Int, val y: Int)

enum class AmphipodType(val energyPerStep: Int, val xPos: Int) {
    A(1, 3),
    B(10, 5),
    C(100, 7),
    D(1000, 9);

    val symbol get() = name.first()
}
