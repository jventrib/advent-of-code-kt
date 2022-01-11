val day11 = day<Int>(11) {
    part1(expectedExampleOutput = 1656, expectedOutput = 1637) {

        val matrix: MutableList<MutableList<Int>> = input.map {
            it.toCharArray().toList().map(Char::digitToInt).toMutableList()
        }.toMutableList()
        val height = matrix.size
        val width = matrix.maxOf { it.size }

        var flashCount = 0
        (1..100).forEach { step ->
            flashCount += doStep(matrix, height, width, step)

        }
        flashCount
    }

    part2(expectedExampleOutput = 195, expectedOutput = 242) {
        val matrix: MutableList<MutableList<Int>> = input.map {
            it.toCharArray().toList().map(Char::digitToInt).toMutableList()
        }.toMutableList()

        val height = matrix.size
        val width = matrix.maxOf { it.size }

        var allFlashStep = 0
        for (step in 1..20000) {
            doStep(matrix, height, width, step)

            if (matrix.flatten().all { it == 0 }) {
                allFlashStep = step
                break
            }
            println(step)
        }
        allFlashStep
    }
}

private fun doStep(
    matrix: MutableList<MutableList<Int>>,
    height: Int,
    width: Int,
    step: Int
): Int {
    var flashCount = 0
    matrix.forEach { y, x -> matrix[y][x] = matrix[y][x] + 1 }
    do {
        val tmpMatrix = (0 until height).map { _ -> (0 until width).map { 0 }.toMutableList() }.toMutableList()
        matrix.forEach { y, x ->
            if (matrix[y][x] == 10) {
                flashCount++
                addFlash(x - 1, y - 1, width, height, tmpMatrix)
                addFlash(x - 1, y, width, height, tmpMatrix)
                addFlash(x - 1, y + 1, width, height, tmpMatrix)
                addFlash(x, y - 1, width, height, tmpMatrix)
                addFlash(x, y, width, height, tmpMatrix)
                addFlash(x, y + 1, width, height, tmpMatrix)
                addFlash(x + 1, y - 1, width, height, tmpMatrix)
                addFlash(x + 1, y, width, height, tmpMatrix)
                addFlash(x + 1, y + 1, width, height, tmpMatrix)
            }
        }
        tmpMatrix.forEach { y, x ->
            val oldValue = matrix[y][x]
            matrix[y][x] += tmpMatrix[y][x]
            if (oldValue < 10 && matrix[y][x] > 10) matrix[y][x] = 10
        }

    } while (matrix.flatten().any { it == 10 })

    matrix.forEach { y, x -> if (matrix[y][x] >= 10) matrix[y][x] = 0 }

    println("step: $step")
    matrix.draw()
    return flashCount
}

private fun MutableList<MutableList<Int>>.forEach(block: (Int, Int) -> Unit) {
    forEachIndexed { y, lines ->
        lines.forEachIndexed { x, v ->
            block(y, x)
        }
    }
}

private fun List<List<Int>>.draw() {
    forEach { line ->
        println(line.joinToString(""))
    }
    println("")
}

private fun addFlash(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    tmpMatrix: MutableList<MutableList<Int>>
) {
    if (x in (0 until width) && y in (0 until height)) tmpMatrix[y][x]++

}