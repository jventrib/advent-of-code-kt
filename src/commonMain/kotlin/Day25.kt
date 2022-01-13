val day25 = day<Int>(25) {
    part1(expectedExampleOutput = 58, expectedOutput = 474) {
        val w = World(input)
        var count = 1
        while(w.doStep()) {
//            w.draw()
            count++
        }
        count
    }

    part2(expectedExampleOutput = 0, expectedOutput = 0) {
        0
    }
}


class World(input: List<String>) {

    private val height: Int
    private val width: Int
    private var grid: Array<CharArray>
    private var grid2: Array<CharArray>

    init {
        height = input.size
        width = input.maxOf { it.toCharArray().size }
        grid = input.map { it.toCharArray() }.toTypedArray()
        grid2 = grid.copy()
    }

    private fun Array<CharArray>.copy() = map { it.copyOf() }.toTypedArray()


    fun doStep(): Boolean {
        val oldGrid = grid.copy()

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (grid[y][x] == '>' && grid[y][x.right()] == '.') {
                    grid2[y][x.right()] = grid[y][x]
                    grid2[y][x] = '.'
                }
            }
        }
        grid = grid2.copy()

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (grid[y][x] == 'v' && grid[y.bellow()][x] == '.') {
                    grid2[y.bellow()][x] = grid[y][x]
                    grid2[y][x] = '.'
                }
            }
        }
        grid = grid2.copy()
        return !oldGrid.contentDeepEquals(grid)
    }

    private fun Int.right() = (this + 1).mod(width)
    private fun Int.bellow() = (this + 1).mod(height)

    fun draw() {
        println()
        grid.forEach { l ->
            l.forEach { c ->
                print(c)
            }
            println()
        }
        println()

    }
}
