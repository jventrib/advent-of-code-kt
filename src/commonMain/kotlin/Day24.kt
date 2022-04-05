import ALU.VarName.*

val day24 = day<Long>(24) {
    part1(expectedExampleOutput = 36969794979199, expectedOutput = 36969794979199) {

        input.doPart(true)


//        val parameters = this.chunked(18).map {
//            Parameters(
//                it[4].substringAfterLast(" ").toInt(),
//                it[5].substringAfterLast(" ").toInt(),
//                it[15].substringAfterLast(" ").toInt()
//            )
//        }

//        val solve = solve(this)
//        solve.second

//        val alu = ALU(this)
//
//        val i = (9 downTo 1).asSequence().flatMap { a ->
//            (9 downTo 1).flatMap { b ->
//                (9 downTo 1).flatMap { c ->
//                    (9 downTo 1).flatMap { d ->
//                        (9 downTo 1).flatMap { e ->
//                            (9 downTo 1).flatMap { f ->
//                                (9 downTo 1).map { g ->
//                                    "$a${b}1$c$d${e}1${f}11111${g}".toLong()
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        val result2 = i.first { alu.run(it) == 0L }
//
//        println(alu.run(36969794979198L).toString())
//        println(alu.run(12959794978298L).toString())
//        println(alu.run(36969794979199L).toString())
//        println(alu.run(13179216111119).toString())
//        println(alu.run(11111114111114).toString())

//        (11111111111111..22222222222222L).forEach {
//            if (it.mod(100000) == 0) println("i: $it -> ${alu.run(it)}")
//        }
//
//        0
    }

    part2(expectedExampleOutput = 11419161313147, expectedOutput = 11419161313147) {
        input.doPart(false)
    }
}

private fun List<String>.doPart(part1: Boolean): Long {
    val allParams = this.chunked(18).map { line ->
        Param(line[5].substringAfterLast(" ").toInt(), line[15].substringAfterLast(" ").toInt())
    }

    val digits = (1..14).toMutableList()
    val stack = mutableListOf<Pair<Int, Int>>()

    allParams.forEachIndexed { i, parameters ->
        if (parameters.check > 0) {
            stack.add(i to parameters.offset)
        } else {
            val (index, offset) = stack.removeLast()
            val newOffset = parameters.check + offset
            val digit = (if (part1) (9 downTo 1) else (1..9)).first { it + newOffset in 1..9 }

            digits[index] = digit
            digits[i] = digit + newOffset
        }
    }

    val result = digits.joinToString("").toLong()
    check(ALU(this).run(result) == 0L)
    return result
}

data class Param(val check: Int, val offset: Int)

class ALU(instStrings: List<String>) {
    private val instructions: List<() -> Unit>
    private lateinit var inputList: ListIterator<Long>

    private val cache = mutableMapOf<Int, MutableMap<Long, Long>>()

    init {
        this.instructions = instStrings.map { it.split(" ") }.map { parseInst(it) }
    }

    fun run(input: Long): Long {
        reset()
        inputList = input.toString().map { it.digitToInt().toLong() }.listIterator()
        instructions.chunked(18).forEachIndexed { index, segment ->
            inp(Operand.Variable(W, this))()
            val wVal = memory.getValue(W)
            val cached = cache[index]?.get(wVal)
            if (cached != null) {
                memory[Z] = cached
            } else {
                segment.drop(1).forEach { it() }
                cache.getOrPut(index) { mutableMapOf() }[wVal] = memory.getValue(Z)
            }
        }
        return memory.getValue(Z)
    }

    private fun reset() {
        memory[W] = 0
        memory[X] = 0
        memory[Y] = 0
        memory[Z] = 0
    }

    private fun parseInst(line: List<String>): () -> Unit {
        fun parseOperand(i: Int): Operand {
            val value = line[i]
            return value.toLongOrNull()?.let { Operand.Literal(it) } ?: Operand.Variable(
                valueOf(value.uppercase()),
                this
            )
        }

        return when (line[0]) {
            "inp" -> inp(parseOperand(1) as Operand.Variable)
            "add" -> add(parseOperand(1), parseOperand(2))
            "mul" -> mul(parseOperand(1), parseOperand(2))
            "div" -> div(parseOperand(1), parseOperand(2))
            "mod" -> mod(parseOperand(1), parseOperand(2))
            "eql" -> eql(parseOperand(1), parseOperand(2))
            else -> error("unknown instructions")
        }
    }

    enum class VarName { W, X, Y, Z }

    val memory = mutableMapOf(
        W to 0L,
        X to 0L,
        Y to 0L,
        Z to 0L,
    )

    private fun Map<VarName, Long>.show() = "w:${this[W]}, x:${this[X]}, y:${this[Y]}, z:${this[Z]}"


    private fun inp(v: Operand.Variable): () -> Unit = {
        memory.show()
        v.setValue(inputList.next())
    }

    private fun add(op1: Operand, op2: Operand): () -> Unit = {
        op1.setValue(op1.getValue() + op2.getValue())
    }

    private fun mul(op1: Operand, op2: Operand): () -> Unit = {
        op1.setValue(op1.getValue() * op2.getValue())
    }

    private fun div(op1: Operand, op2: Operand): () -> Unit = {
        op1.setValue(op1.getValue() / op2.getValue())
    }

    private fun mod(op1: Operand, op2: Operand): () -> Unit = {
        op1.setValue(op1.getValue() % op2.getValue())
    }

    private fun eql(op1: Operand, op2: Operand): () -> Unit = {
        op1.setValue(if (op1.getValue() == op2.getValue()) 1 else 0)
    }


    sealed interface Operand {
        fun getValue(): Long
        fun setValue(i: Long)

        class Literal(private val v: Long) : Operand {
            override fun getValue() = v
            override fun setValue(i: Long) {
                error("cannot set literal value")
            }
        }

        class Variable(private val varName: VarName, private val alu: ALU) : Operand {
            override fun getValue(): Long = alu.memory.getValue(varName)

            override fun setValue(i: Long) {
                alu.memory[varName] = i
            }
        }
    }
}


