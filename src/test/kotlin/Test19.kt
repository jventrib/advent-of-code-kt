import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Test19 {

    @Test
    fun testMatrixTimePoint() {
        assertRotated(Point3d(1, -1, 1), Point3d(-1, -1, 1))
        assertRotated(Point3d(2, -2, 2), Point3d(-2, -2, 2))
        assertRotated(Point3d(3, -3, 3), Point3d(-3, -3, 3))
        assertRotated(Point3d(2, -1, 3), Point3d(-2, -3, 1))
        assertRotated(Point3d(-5, 4, -6), Point3d(5, 6, -4))
        assertRotated(Point3d(-8, -7, 0), Point3d(8, 0, 7))

        assertRotated(Point3d(1, -1, 1), Point3d(-1, -1, 1))
        assertRotated(Point3d(2, -2, 2), Point3d(-2, -2, 2))
        assertRotated(Point3d(3, -3, 3), Point3d(-3, -3, 3))
        assertRotated(Point3d(2, -1, 3), Point3d(-2, -3, 1))
        assertRotated(Point3d(-5, 4, -6), Point3d(5, 6, -4))
        assertRotated(Point3d(-8, -7, 0), Point3d(8, 0, 7))

        assertRotated(Point3d(-1, -1, -1), Point3d(-1, -1, 1))
        assertRotated(Point3d(-2, -2, -2), Point3d(-2, -2, 2))
        assertRotated(Point3d(-3, -3, -3), Point3d(-3, -3, 3))
        assertRotated(Point3d(-1, -3, -2), Point3d(-2, -3, 1))
        assertRotated(Point3d(4, 6, 5), Point3d(5, 6, -4))
        assertRotated(Point3d(-7, 0, 8), Point3d(8, 0, 7))

        assertRotated(Point3d(1, 1, -1), Point3d(-1, -1, 1))
        assertRotated(Point3d(2, 2, -2), Point3d(-2, -2, 2))
        assertRotated(Point3d(3, 3, -3), Point3d(-3, -3, 3))
        assertRotated(Point3d(1, 3, -2), Point3d(-2, -3, 1))
        assertRotated(Point3d(-4, -6, 5), Point3d(5, 6, -4))
        assertRotated(Point3d(7, 0, 8), Point3d(8, 0, 7))

        assertRotated(Point3d(1, 1, 1), Point3d(-1, -1, 1))
        assertRotated(Point3d(2, 2, 2), Point3d(-2, -2, 2))
        assertRotated(Point3d(3, 3, 3), Point3d(-3, -3, 3))
        assertRotated(Point3d(3, 1, 2), Point3d(-2, -3, 1))
        assertRotated(Point3d(-6, -4, -5), Point3d(5, 6, -4))
        assertRotated(Point3d(0, 7, -8), Point3d(8, 0, 7))
    }

    private fun assertRotated(expected: Point3d, point: Point3d) {
        assertTrue(orientations.map { it * point }.any { expected == it })
    }


    @Test
    fun testParallelFlow() {
        val f: Flow<String> = (0 until 100).asFlow().concurrentMap(Dispatchers.Default, 1) { i ->
            bigTask(i)
        }


        runBlocking {
            f.collect {
                println(it)
            }
        }
    }

    private fun bigTask(i: Int): String {
        Thread.sleep(1000)
        return "${Thread.currentThread().name}: ${i * i}"
    }
}

