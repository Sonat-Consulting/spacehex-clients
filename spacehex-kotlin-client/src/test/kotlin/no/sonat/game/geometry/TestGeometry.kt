package no.sonat.game.geometry

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestGeometry {

    @Test
    fun testSegmentIntersect() {
        val segmA = LineSegment2D(Vec2D(-1.0,-1.0), Vec2D(1.0,1.0))
        val segmB = LineSegment2D(Vec2D(-1.0,1.0),Vec2D(1.0,-1.0))
        val pt = segmA.intersects(segmB)
        assertTrue { pt != null }
        assertTrue { ( pt!!- Vec2D.ZERO).length() < 0.01 }
    }

    @Test
    fun testSegmentNotIntersect() {
        val segmA = LineSegment2D(Vec2D(-1.0,-1.0), Vec2D(1.0,1.0))
        val segmB = LineSegment2D(Vec2D(-2.0,-2.0),Vec2D(-1.5,-1.5))
        val pt = segmA.intersects(segmB)
        assertTrue { pt == null }
    }

    @Test
    fun testSegmentNotIntersectIfParallel() {
        val segmA = LineSegment2D(Vec2D(-1.0,-1.0), Vec2D(1.0,1.0))
        val segmB = LineSegment2D(Vec2D(-2.0,-2.0),Vec2D(-1.0,-1.0))
        val pt = segmA.intersects(segmB)
        assertTrue { pt == null }
    }

    @Test
    fun testSegmentIntersectInEndPoint() {
        val segmA = LineSegment2D(Vec2D(-1.0,1.0), Vec2D(1.0,1.0))
        val segmB = LineSegment2D(Vec2D(-2.0,-2.0), Vec2D(1.0,1.0))
        val pt = segmA.intersects(segmB)
        assertTrue { pt != null }
        assertTrue { ( pt!!- Vec2D(1.0,1.0)).length() < 0.01 }
    }

    @Test
    fun testSegmentIntersectInPoint() {
        val segmA = LineSegment2D(Vec2D(-2.0,-2.0), Vec2D(1.0,1.0))
        val segmB = LineSegment2D(Vec2D(1.0,1.0), Vec2D(3.0,-1.0))
        val pt = segmA.intersects(segmB)
        assertTrue { pt != null }
        assertTrue { ( pt!!- Vec2D(1.0,1.0)).length() < 0.01 }
    }

    @Test
    fun testSegmentNoIntersectCase() {
        val segmA = LineSegment2D(start=Vec2D(x=-341.0, y=-223.0), end=Vec2D(x=-292.0, y=-194.0))
        val segmB = LineSegment2D(start=Vec2D(x=445.8586189801885, y=246.7999999999996), end=Vec2D(x=447.3242977416373, y=243.5499999999996))

        val pt = segmA.intersects(segmB)
        assertTrue { pt == null }
    }

}