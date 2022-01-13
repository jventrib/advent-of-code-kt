import SFNumber.*
import kotlin.math.ceil

val day18 = day<Int>(18) {
    part1(expectedExampleOutput = 4140, expectedOutput = 3411) {
        val result =
            input.map { SFNumber.parse(it) }.reduce { acc: SFPair, e: SFNumber -> acc + e }

        result.magnitude()

    }

    part2(expectedExampleOutput = 3993, expectedOutput = 4680) {
        input.map { SFNumber.parse(it) }
        val sums = input.flatMap { a ->
            input.map { b ->
                SFNumber.parse(a) + SFNumber.parse(b)
            }
        }
        val maxOf = sums.maxOf { it.magnitude() }
        maxOf
    }
}


sealed class SFNumber {
    var parent: SFPair? = null

    abstract fun magnitude(): Int

    class SFRegular(var value: Int) : SFNumber() {

        override fun magnitude() = value

        override fun toString() = "$value"

        fun split(): SFPair {
            return SFPair(SFRegular(value / 2), SFRegular(ceil(value / 2.0).toInt())).also {
                it.left.parent = it
                it.right.parent = it
            }
        }
    }

    class SFPair(var left: SFNumber, var right: SFNumber) : SFNumber() {
        fun explode(toExplode: SFPair) {
            val par = toExplode.parent!!
            val tmpLeft = (toExplode.left as SFRegular).value
            val tmpRight = (toExplode.right as SFRegular).value
            val list = this.flatten()
            val leftRegular = list.getOrNull(list.indexOf(toExplode.left) - 1) as SFRegular?
            val rightRegular = list.getOrNull(list.indexOf(toExplode.right) + 1) as SFRegular?
            if (leftRegular != null) {
                leftRegular.value += tmpLeft
            }
            if (rightRegular != null) {
                rightRegular.value += tmpRight
            }
            val exploded = SFRegular(0).also { it.parent = par }
            if (par.left == toExplode) par.left = exploded
            if (par.right == toExplode) par.right = exploded
        }

        fun split(toSplit: SFRegular) {
            val par = toSplit.parent!!
            val splitted = toSplit.split()
            splitted.parent = par
            if (par.left == toSplit) par.left = splitted
            if (par.right == toSplit) par.right = splitted
        }

        fun findToExplode(number: SFNumber = this, depth: Int = 0): SFPair? {
            return if (number is SFPair && depth == 4) number
            else if (number is SFRegular) null
            else if ((number as SFPair).left is SFPair) {
                val findNested = findToExplode(number.left, depth + 1)
                findNested ?: findToExplode(number.right, depth + 1)
            } else if (number.right is SFPair) findToExplode(number.right, depth + 1)
            else null
        }

        private fun findToSplit(): SFRegular? {
            return this.flatten().filterIsInstance<SFRegular>().firstOrNull { it.value >= 10 }
        }

        override fun magnitude() = left.magnitude() * 3 + right.magnitude() * 2

        override fun toString() = "[$left,$right]"

        operator fun plus(other: SFNumber): SFPair {
            val additionResult = SFPair(this, other).also {
                it.left.parent = it
                it.right.parent = it
            }
            var toExplode: SFPair?
            var toSplit: SFRegular?
            do {
                toExplode = additionResult.findToExplode()
                toSplit = additionResult.findToSplit()
                toExplode?.let { additionResult.explode(it) }
                if (toSplit != null && toExplode == null) toSplit.let { additionResult.split(toSplit) }

            } while (toExplode != null || toSplit != null)
            return additionResult
        }
    }

    fun flatten(list: List<SFNumber> = listOf()): List<SFNumber> {
        if (this is SFPair) {
            return list.plus(left.flatten()).plus(right.flatten())
        }
        return list.plus(this)
    }

    companion object {
        fun parse(pairString: String): SFPair {
            return parseInternal(pairString) as SFPair
        }

        private fun parseInternal(pairString: String): SFNumber {
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
                val left = parseInternal(inPair[0].removeSuffix(","))
                val right = parseInternal(inPair[1].removePrefix(","))
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

