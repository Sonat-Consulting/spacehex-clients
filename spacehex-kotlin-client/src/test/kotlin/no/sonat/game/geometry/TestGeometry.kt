package no.sonat.game.geometry

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TestGeometry {

    @Test
    fun testSegmentIntersect() {
        val segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        val segmB = LineSegment2D(Vec2D(-1.0, 1.0), Vec2D(1.0, -1.0))
        val pt = segmA.intersects(segmB)
        checkAllIntersects(segmA, segmB)
        assertTrue { (pt!! - Vec2D.ZERO).length() < 0.01 }
    }

    @Test
    fun testSegmentNotIntersect() {
        val segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        val segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(-1.5, -1.5))
        checkAllNotIntersects(segmA, segmB)
    }

    private fun checkAllNotIntersects(a: LineSegment2D, b: LineSegment2D) {
        assertTrue("Does intersect $a,$b") { a.intersects(b) == null }
        assertTrue("Does intersect $a,$b") { a.swap().intersects(b) == null }
        assertTrue("Does intersect $a,$b") { a.intersects(b.swap()) == null }
        assertTrue("Does intersect $a,$b") { a.swap().intersects(b.swap()) == null }
    }

    private fun checkAllIntersects(a: LineSegment2D, b: LineSegment2D) {
        assertTrue("Does not intersect $a,$b") { a.intersects(b) != null }
        assertTrue("Does not intersect $a,$b") { a.swap().intersects(b) != null }
        assertTrue("Does not intersect $a,$b") { a.intersects(b.swap()) != null }
        assertTrue("Does not intersect $a,$b") { a.swap().intersects(b.swap()) != null }
    }

    @Test
    fun testSegmentIntersectIfParallel() {
        var segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        var segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(-1.0, -1.0))
        checkAllIntersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(2.0, 2.0))
        checkAllIntersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-0.5, -0.5), Vec2D(2.0, 2.0))
        checkAllIntersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-0.5, -0.5), Vec2D(-0.5, -0.5))
        checkAllIntersects(segmA, segmB)
    }


    @Test
    fun testSegmentIntersectInEndPoint() {
        val segmA = LineSegment2D(Vec2D(-1.0, 1.0), Vec2D(1.0, 1.0))
        val segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(1.0, 1.0))
        val pt = segmA.intersects(segmB)
        checkAllIntersects(segmA, segmB)
        assertTrue { (pt!! - Vec2D(1.0, 1.0)).length() < 0.01 }
    }

    @Test
    fun testSegmentIntersectInPoint() {
        val segmA = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(1.0, 1.0))
        val segmB = LineSegment2D(Vec2D(1.0, 1.0), Vec2D(3.0, -1.0))
        val pt = segmA.intersects(segmB)
        checkAllIntersects(segmA, segmB)
        assertTrue { (pt!! - Vec2D(1.0, 1.0)).length() < 0.01 }
    }

    @Test
    fun testSegmentNoIntersectCase() {
        val segmA = LineSegment2D(start = Vec2D(x = -341.0, y = -223.0), end = Vec2D(x = -292.0, y = -194.0))
        val segmB = LineSegment2D(
            start = Vec2D(x = 445.8586189801885, y = 246.7999999999996),
            end = Vec2D(x = 447.3242977416373, y = 243.5499999999996)
        )
        checkAllNotIntersects(segmA, segmB)
    }


    @Test
    fun testSegmentNormalVectorIntersectAll() {
        //All normals at mid point intersect
        (0..100).forEach {
            val nX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val nY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val eX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val eY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val segmA = LineSegment2D(start = Vec2D(x = nX, y = nY), end = Vec2D(x = nX + eX, y = nY + eY))
            val vec = segmA.vector()
            val norm = Vec2D(vec.y, vec.x).unit()
            val segmB = LineSegment2D(
                start = (segmA.start + segmA.end) / 2.0 - norm * 20.0,
                end = (segmA.start + segmA.end) / 2.0 + norm * 20.0
            )
            checkAllIntersects(segmA, segmB)
        }
    }


    @Test
    fun testSegmentIntersectAll() {
        //All normals at mid point intersect
        (0..100).forEach {
            val nX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val nY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val eX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val eY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val segmA = LineSegment2D(start = Vec2D(x = nX, y = nY), end = Vec2D(x = nX + eX, y = nY + eY))

            val vec = segmA.vector()
            val sign = Random.nextSign()
            val normA = Vec2D(vec.y, vec.x).unit() * sign * (Math.random() * 100)
            val normB = Vec2D(vec.y, vec.x).unit() * -sign * (Math.random() * 100)
            val segmB = LineSegment2D(
                start = (segmA.start + segmA.vector() * Math.random()) + normA,
                end = (segmA.start + segmA.vector() * Math.random()) + normB
            )
            checkAllIntersects(segmA, segmB)
        }
    }

    private fun Random.nextSign(): Double {
        return if (Random.nextBoolean()) -1.0 else 1.0
    }

    @Test
    fun testSegmentParallelAndNoIntersect() {
        //No intersect since parallel line moved away from original
        (0..100).forEach {
            val nX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val eX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val segmA = LineSegment2D(start = Vec2D(x = nX, y = nX), end = Vec2D(x = nX + eX, y = eX))
            val norm = segmA.vector().normalVector() * Random.nextSign() * (Random.nextDouble() + 0.1) * 1000.0

            val vecDir = segmA.vector() * (Random.nextDouble() + 0.1) * 1000.0
            val segmB = LineSegment2D(
                start = Vec2D(x = nX, y = nX) + norm + vecDir,
                end = Vec2D(x = nX + eX, y = eX) + norm + vecDir
            )
            checkAllNotIntersects(segmA, segmB)
        }
    }


    @Test
    fun testParallellIntersection() {
        //All normals at mid point intersect
        (0..100).forEach {
            val nX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val nY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val eX = (Math.random() + 0.1) * 10.0 * Random.nextSign()
            val eY = (Math.random() + 0.1) * 10.0 * Random.nextSign()

            val segmA = LineSegment2D(start = Vec2D(x = nX, y = nY), end = Vec2D(x = eX, y = eY))
            val vec = segmA.vector()
            val norm = Vec2D(vec.y, vec.x).unit()
            val segmB = LineSegment2D(
                start = (segmA.start + segmA.end) / 2.0 - norm * 20.0,
                end = (segmA.start + segmA.end) / 2.0 + norm * 20.0
            )
            checkAllIntersects(segmA, segmB)
        }
    }


    @Test
    fun inSegmentSingle() {
        val segm2 = LineSegment2D(start = Vec2D(x = -2.0, y = -1.0), end = Vec2D(x = 2.0, y = 1.0))

        val pt = Vec2D(-0.9423157439768692, -0.4711578719884346)
        assertTrue("$pt not in segment") { segm2.inSegment(pt) }
    }

    @Test
    fun testInSegment() {
        val segmA = LineSegment2D(start = Vec2D(x = -1.0, y = -1.0), end = Vec2D(x = 2.0, y = 2.0))

        (0..100).forEach { _ ->
            val n = (Math.random() - 0.5) * 2
            val pt = Vec2D(n, n)
            assertTrue("$pt not in segment") { segmA.inSegment(pt) }
        }

        val segm2 = LineSegment2D(start = Vec2D(x = -2.0, y = -1.0), end = Vec2D(x = 2.0, y = 1.0))

        (0..100).forEach { _ ->
            val n = (Math.random() - 0.5) * 2
            val pt = Vec2D(2 * n, n)
            assertTrue("$pt not in segment") { segm2.inSegment(pt) }
        }

        val segm3 = LineSegment2D(start = Vec2D(x = -2.0, y = 0.0), end = Vec2D(x = 2.0, y = 0.0))

        (0..100).forEach { _ ->
            val n = (Math.random() - 0.5) * 2
            val pt = Vec2D(2 * n, 0.0)
            assertTrue("$pt not in segment") { segm3.inSegment(pt) }
        }


        val segm4 = LineSegment2D(start = Vec2D(x = 0.0, y = -2.0), end = Vec2D(x = 0.0, y = 2.0))

        (0..100).forEach { _ ->
            val n = (Math.random() - 0.5) * 2
            val pt = Vec2D(0.0, 2 * n,)
            assertTrue("$pt not in segment") { segm4.inSegment(pt) }
        }
    }


    @Test
    fun caseNotInSegment() {
        val segmA = LineSegment2D(start = Vec2D(x = -1.0, y = -1.0), end = Vec2D(x = 2.0, y = 2.0))
        val pt = Vec2D(-9.829257132209216, -9.829257132209216)
        assertFalse("$pt in segment") { segmA.inSegment(pt) }

        val pt2 = Vec2D(9.829257132209216, 9.829257132209216)
        assertFalse("$pt2 in segment") { segmA.inSegment(pt2) }

        val pt3 = Vec2D(2.02, 2.02)
        assertFalse("$pt3 in segment") { segmA.inSegment(pt3) }

        val pt4 = Vec2D(-1.01, 1.01)
        assertFalse("$pt3 in segment") { segmA.inSegment(pt4) }
    }

    @Test
    fun testNotInSegment() {
        val segmA = LineSegment2D(start = Vec2D(x = -1.0, y = -1.0), end = Vec2D(x = 2.0, y = 2.0))

        (0..100).forEach {
            val n = Math.random() + 0.1
            val pt = Vec2D(n, n + 0.25)
            assertFalse("$pt in segment") { segmA.inSegment(pt) }
        }

        (0..100).forEach {
            val n = Math.random()
            val pt = Vec2D(n - 10, n - 10)
            assertFalse("$pt in segment") { segmA.inSegment(pt) }
        }

        val segm2 = LineSegment2D(start = Vec2D(x = -2.0, y = -1.0), end = Vec2D(x = 2.0, y = 1.0))

        (0..100).forEach {
            val n = (Math.random() - 0.5) * 2
            val pt = Vec2D(2 * n, n + 0.3)
            assertFalse("$pt in segment") { segm2.inSegment(pt) }
        }

    }

    @Test
    fun testClosestPoint() {
        val segmA = LineSegment2D(start = Vec2D(x = -1.0, y = -1.0), end = Vec2D(x = 2.0, y = 2.0))

        var closest = segmA.closestPoint(Vec2D(3.0, 3.0))
        assertTrue("Closest point was $closest") { (closest - Vec2D(2.0, 2.0)).length() < 0.01 }

        closest = segmA.closestPoint(Vec2D(-2.0, -2.0))
        assertTrue("Closest point was $closest") { (closest - Vec2D(-1.0, -1.0)).length() < 0.01 }

        closest = segmA.closestPoint(Vec2D(0.0, 0.0))
        assertTrue("Closest point was $closest") { (closest - Vec2D(0.0, 0.0)).length() < 0.01 }

        closest = segmA.closestPoint(Vec2D(0.0, 0.0))
        assertTrue("Closest point was $closest") { (closest - Vec2D(0.0, 0.0)).length() < 0.01 }
    }

    @Test
    fun testClosestPointCase() {
        val segmA = LineSegment2D(start = Vec2D(x = -1.0, y = -1.0), end = Vec2D(x = 2.0, y = 2.0))

        (0..100).forEach {
            val v = segmA.vector()
            val n = v.normalVector() * Random.nextSign()

            val p = segmA.start + v * Math.random() //Pick a point along the line
            val check = p + n * 100.0 //Move along the normal from that point

            val closest = segmA.closestPoint(check)
            assertTrue("Closest point was $closest expected $p for $check") { (closest - p).length() < 0.01 }
        }
        (0..100).forEach {
            val v = segmA.vector()
            val n = v.normalVector() * Random.nextSign()

            val p = segmA.start + v * (Math.random() * -100) //Pick a point before start
            val check = p + n * 100.0 //Move along the normal from that point

            val closest = segmA.closestPoint(check)
            assertTrue("Closest point was $closest expected $p for $check") { (closest - segmA.start).length() < 0.01 }
        }
        (0..100).forEach {
            val v = segmA.vector()
            val n = v.normalVector() * Random.nextSign()

            val p = segmA.start + v * (1 + Math.random() * 100) //Pick a point after end
            val check = p + n * 100.0 //Move along the normal from that point

            val closest = segmA.closestPoint(check)
            assertTrue("Closest point was $closest expected $p for $check") { (closest - segmA.end).length() < 0.01 }
        }
    }


}
