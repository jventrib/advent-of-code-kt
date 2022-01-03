import ALU.VarName.*

val day24 = day<Long>(24) {
    part1(expectedExampleOutput = 36969794979199, expectedOutput = 36969794979199) {

        doPart(true)


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
        doPart(false)

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
            val digit = if (part1) {
                (9 downTo 1).first { it + newOffset in 1..9 }
            } else {
                (1..9).first { it + newOffset in 1..9 }
            }
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
    private val insts: List<() -> Unit>
    private lateinit var inputList: ListIterator<Long>

    private val cache = mutableMapOf<Int, MutableMap<Long, Long>>()

    init {
        this.insts = instStrings.map { it.split(" ") }.map { parseInst(it) }
    }

    fun run(input: Long): Long {
        reset()
        inputList = input.toString().toCharArray().map { it.digitToInt().toLong() }.listIterator()
        insts.chunked(18).forEachIndexed { index, segment ->
            inp(Operand.Variable(w, this))()
            val wVal = memory.getValue(w)
            val cached = cache[index]?.get(wVal)
            if (cached != null) {
                memory[z] = cached
            } else {
                segment.drop(1).forEach { it() }
                cache.getOrPut(index) { mutableMapOf() }[wVal] = memory.getValue(z)
            }
        }
        return memory.getValue(z)
    }

    private fun reset() {
        memory[w] = 0
        memory[x] = 0
        memory[y] = 0
        memory[z] = 0
    }

    private fun parseInst(line: List<String>): () -> Unit {
        fun parseOperand(i: Int): Operand {
            val value = line[i]
            return value.toLongOrNull()?.let { Operand.Literal(it) } ?: Operand.Variable(valueOf(value), this)
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

    enum class VarName { w, x, y, z }

    val memory = mutableMapOf(
        w to 0L,
        x to 0L,
        y to 0L,
        z to 0L,
    )

    private fun Map<VarName, Long>.show() = "w:${this[w]}, x:${this[x]}, y:${this[y]}, z:${this[z]}"


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

        class Literal(val v: Long) : Operand {
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


private fun getMagicParameters(input: List<String>): List<Parameters> = parseMagicParameters(input)

private class Parameters(val a: Int, val b: Int, val c: Int)

private fun parseMagicParameters(input: List<String>): List<Parameters> =
    input.chunked(18).map {
        Parameters(
            it[4].substringAfterLast(" ").toInt(),
            it[5].substringAfterLast(" ").toInt(),
            it[15].substringAfterLast(" ").toInt()
        )
    }


private fun magicFunction(parameters: Parameters, z: Long, w: Long): Long =
    if (z % 26 + parameters.b != w) ((z / parameters.a) * 26) + w + parameters.c
    else z / parameters.a


private fun solve(input: List<String>): Pair<Long, Long> {
    var zValues = mutableMapOf(0L to (0L to 0L))
    getMagicParameters(input).forEach { parameters ->
        val zValuesThisRound = mutableMapOf<Long, Pair<Long, Long>>()
        zValues.forEach { (z, minMax) ->
            (1..9).forEach { digit ->
                val newValueForZ = magicFunction(parameters, z, digit.toLong())
                if (parameters.a == 1 || (parameters.a == 26 && newValueForZ < z)) {
                    zValuesThisRound[newValueForZ] =
                        minOf(zValuesThisRound[newValueForZ]?.first ?: Long.MAX_VALUE, minMax.first * 10 + digit) to
                                maxOf(
                                    zValuesThisRound[newValueForZ]?.second ?: Long.MIN_VALUE,
                                    minMax.second * 10 + digit
                                )
                }
            }
        }
        zValues = zValuesThisRound
    }
    return zValues.getValue(0)
}
