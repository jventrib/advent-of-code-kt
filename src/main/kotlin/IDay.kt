interface IDay<E> {

    fun day(): Int

    val input: List<String>

    fun part1(): PartResult<E>

    fun part2(): PartResult<E>

    fun E.shouldBe(expectedExampleOutput: E, expectedOutput: E) =
        PartResult(this, expectedExampleOutput, expectedOutput)
}