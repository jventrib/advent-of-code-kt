val day20 = day<Int>(20) {
    part1(expectedExampleOutput = 35, expectedOutput = 5349) {
        doPart(2)
    }

    part2(expectedExampleOutput = 3351, expectedOutput = 15806) {
        doPart(50)
    }
}

private fun List<String>.doPart(step: Int): Int {
    val enhancement = this.first()
    val image = getInput()

    val finalImage = (0 until step).fold(image) { acc, _ ->
        acc.processImage(enhancement)
    }

    finalImage.draw()
    return finalImage.pixels.flatten().count { it == '#' }
}

private fun List<String>.getInput(): Image {
    val image = Image(this.dropWhile { it.isNotEmpty() }.drop(1).map { line ->
        line.toCharArray().toList()
    })
    return image
}

class Image(val pixels: List<List<Char>>, private val infinitePixel: Char = '.') {

    fun processImage(enhancement: String): Image {
        val infiniteIndex = List(9) { infinitePixel }.getIndex()
        val newInfinitePixel = enhancement[infiniteIndex]
        val margin = 1
        val height = -(1 + margin)..pixels.size + margin
        val width = -(1 + margin)..pixels.maxOf { it.size } + margin
        return Image(height.map { y ->
            width.map { x ->
                val index = getSurroundingPixels(x, y)
                enhancement[index]
            }
        }, newInfinitePixel)
    }

    private fun getSurroundingPixels(x: Int, y: Int): Int {
        val tl = pixel(y - 1, x - 1)
        val t = pixel(y - 1, x)
        val tr = pixel(y - 1, x + 1)
        val ml = pixel(y, x - 1)
        val m = pixel(y, x)
        val mr = pixel(y, x + 1)
        val bl = pixel(y + 1, x - 1)
        val b = pixel(y + 1, x)
        val br = pixel(y + 1, x + 1)

        val listOf = listOf(tl, t, tr, ml, m, mr, bl, b, br)
        return listOf.getIndex()
    }

    private fun List<Char>.getIndex() = map { if (it == '#') 1 else 0 }.joinToString("").toInt(2)

    private fun pixel(y: Int, x: Int) = pixels.getOrNull(y)?.getOrNull(x) ?: infinitePixel

    fun draw() {
        val margin = 1
        val width = pixels.maxOf { it.size } * margin
        val height = pixels.size * margin

        println()
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                print(pixels.getOrNull(y)?.getOrNull(x) ?: '.')
            }
            println()
        }
        println()
    }

    override fun toString(): String {
        return "Size ${this.pixels.maxOf { it.size }} x ${this.pixels.size}"
    }
}


