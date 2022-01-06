import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Test23 {

    @Test
    fun testAmphipod() {
        val amp1 = Amphipod(AmphipodType.A, Pos(1, 2), 20)
        val amp2 = Amphipod(AmphipodType.A, Pos(1, 2), 20)
        assertEquals(amp2, amp1)

    }

    @Test
    fun testWorld23() {
        val amp1 = Amphipod(AmphipodType.A, Pos(1, 2), 2)
        val amp2 = Amphipod(AmphipodType.B, Pos(2, 3), 20)
        val w1 = World23(setOf(amp1, amp2), null, 20)

        val amp21 = Amphipod(AmphipodType.A, Pos(1, 2), 2)
        val amp22 = Amphipod(AmphipodType.B, Pos(2, 3), 20)
        val w2 = World23(setOf(amp21, amp22), null, 20)


        assertEquals(w2,w1)

    }
}
