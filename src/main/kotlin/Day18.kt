val day18 = day<Int>(18) {
    part1(expectedExampleOutput = 0, expectedOutput = 0) {
        SFNumber.parse("[[1,2],3]")
        0

    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        0
    }
}


sealed class SFNumber {

    var parent: SFPair? = null

    class SFRegular(var value: Int) : SFNumber() {
        override fun toString() = "$value"
    }

    class SFPair(var left: SFNumber, var right: SFNumber) : SFNumber() {
        fun explode() {
            val nested = findNested(this)
            println(nested)

            if (nested != null && nested is SFPair) {
                val par = nested.parent
                val tmpLeft = (nested.left as SFRegular).value
                val tmpRight = (nested.right as SFRegular).value
                val list = this.flatten()
                val leftRegular = list.getOrNull(list.indexOf(nested.left) - 1) as SFRegular?
                val rightRegular = list.getOrNull(list.indexOf(nested.right) + 1) as SFRegular?
                if (leftRegular != null) {
                    leftRegular.value += tmpLeft
                }
                if (rightRegular != null) {
                    rightRegular.value += tmpRight
                }
                if (nested.parent?.left == nested) nested.parent?.left = SFRegular(0).apply { parent = par }
                if (nested.parent?.right == nested) nested.parent?.right = SFRegular(0).apply { parent = par }
                println(nested)

            }
        }

        private fun findNested(number: SFNumber, depth: Int = 0): SFNumber? {
            return if (number is SFPair && depth == 4) number
            else if (number is SFRegular) null
            else if ((number as SFPair).left is SFPair) {
                val findNested = findNested(number.left, depth + 1)
                findNested ?: findNested(number.right, depth + 1)
            } else if (number.right is SFPair) findNested(number.right, depth + 1)
            else null
        }

        override fun toString() = "[$left,$right]"

    }

    operator fun plus(other: SFNumber) = SFPair(this, other)

    fun flatten(list: List<SFNumber> = listOf()): List<SFNumber> {
        if (this is SFPair) {
            return list.plus(left.flatten()).plus(right.flatten())
        }
        return list.plus(this)
    }

    fun getLeftRegular(): SFRegular? {
        if (this.parent == null) return null
        if (this.parent?.left is SFRegular) return this.parent?.left as SFRegular
        return this.parent?.getLeftRegular()
    }

    fun getRightRegular(): SFRegular? {
        if (this.parent == null) return null
        if (this.parent?.right is SFRegular) return this.parent?.right as SFRegular
        return this.parent?.getRightRegular()
    }


    companion object {
        fun parse(pairString: String): SFNumber {
            if (pairString.first() == '[') {
                var depth = 0
                val inPair = mutableListOf("", "")
                var pairIndex = 0

                pairString.drop(1).dropLast(1).forEach { c ->
                    inPair[pairIndex] = inPair[pairIndex] + c
                    when {
                        c == '[' -> depth++
                        c == ']' -> depth--
                        depth == 0 && c == ',' -> pairIndex++
                    }
                }
                val left = parse(inPair[0].removeSuffix(","))
                val right = parse(inPair[1].removePrefix(","))
                val sfPair = SFPair(left, right)
                left.parent = sfPair
                right.parent = sfPair
                return sfPair
            }

            val sfRegular = SFRegular(pairString.toInt())

            return sfRegular
        }
    }
}

