import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Test22 {

    @Test
    fun testIntersection() {

        assertEquals(3, 3L..11L int 9L..18L)
        assertEquals(3, 9L..18L int 3L..11L)
        assertEquals(0, 3L..11L int 13L..18L)
        assertEquals(6, 3L..18L int 13L..18L)

    }
}
