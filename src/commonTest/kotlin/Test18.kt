import kotlin.test.Test
import kotlin.test.assertEquals

class Test18 {

    @Test
    fun testAdd() {
        val op1 = SFNumber.parse("[[[[4,3],4],4],[7,[[8,4],9]]]")
        val op2 = SFNumber.parse("[1,1]")

        val result = op1 + op2

        val expected = SFNumber.parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
        assertEquals(expected.toString(), result.toString())

    }

    @Test
    fun testAdd2() {
        val op1 = SFNumber.parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]")
        val op2 = SFNumber.parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]")

        val result = op1 + op2

        val expected = SFNumber.parse("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]")
        assertEquals(expected.toString(), result.toString())

    }


    @Test
    fun testExplode() {
        testExplode("[[[[[9,8],1],2],3],4]", "[[[[0,9],2],3],4]")
        testExplode("[7,[6,[5,[4,[3,2]]]]]", "[7,[6,[5,[7,0]]]]")
        testExplode("[[6,[5,[4,[3,2]]]],1]", "[[6,[5,[7,0]]],3]")
        testExplode("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]", "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        testExplode("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
    }

    private fun testExplode(inputS: String, expectedS: String) {
        val input = SFNumber.parse(inputS)
        val expected = SFNumber.parse(expectedS)
        val sfPair = input
        sfPair.explode(sfPair.findToExplode()!!)
        assertEquals(expected.toString(), sfPair.toString())
    }

    @Test
    fun testSplit() {
        assertEquals(SFNumber.parse("[5,6]").toString(), SFNumber.SFRegular(11).split().toString())
        assertEquals(SFNumber.parse("[6,6]").toString(), SFNumber.SFRegular(12).split().toString())
    }

    @Test
    fun testParse() {
        val parse = SFNumber.parse("[[1,2],3]")
        println(parse)
    }

    @Test
    fun testParse2() {
        val parse = SFNumber.parse("[9,[8,7]]")
        println(parse)
    }

    @Test
    fun testParse3() {
        val parse2 = SFNumber.parse("[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]")
        println(parse2.toString())
    }
}