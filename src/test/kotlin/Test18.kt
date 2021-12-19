import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Test18 {

//    @Test
//    fun testAdd() {
//        val result = n(1, 2) + n(n(3, 4), n(5))
//        assertEquals(n(n(1, 2), n(n(3, 4), n(5))), result)
//    }


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
        val sfPair = input as SFNumber.SFPair
        sfPair.explode()
        assertEquals(expected.toString(), sfPair.toString())
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