package no.sonat.game.geometry

import kotlin.*
import kotlin.math.*

/**
 * Used for FP number comparisons
 * So we can control accuracy outside of pure FP accuracy.
 */
const val EPSILON = 0.0000001

/**
 * Represents a line segment between the points start and end
 * @param start start point
 * @param end end point
 */
class LineSegment2D(val start : Vec2D, val end : Vec2D) {

    fun swap() : LineSegment2D {
        return LineSegment2D(end,start)
    }

    /**
     * Vector from start to end
     */
    fun vector() : Vec2D {
        return end - start;
    }

    /**
     * Length (euclidian) of the segment
     */
    fun length() : Double {
        return (end-start).length()
    }

    /**
     * Vector of size 1 in direction start to end
     */
    fun direction() : Vec2D {
        return (end-start).unit()
    }

    /**
     * The closest point on this line segment to the input point
     */
    fun closestPoint(pt: Vec2D) : Vec2D {
        val r = vector()

        val t = ((pt.x*r.x - start.x*r.x) + (pt.y*r.y - start.y*r.y))/(r.y*r.y+r.x*r.x)
        val ptOnSegm = start+r*t

        return if(inSegment(ptOnSegm)) {
            return ptOnSegm
        }
        else {
            val a = (start-ptOnSegm).length()
            val b = (end-ptOnSegm).length()
            if( a < b) {
                start
            }
            else {
                end
            }
        }
    }

    /**
     * Does this line segment contain the given pt
     */
    fun inSegment(pt : Vec2D) : Boolean {
        val v = vector()
        val t = pt-start
        if(abs(t.y*v.x-t.x*v.y) < EPSILON) {
            val a = (t.x == 0.0 || (t.x/v.x) in (0.0 - EPSILON) .. (1.0+ EPSILON))
            val b = (t.y == 0.0 || (t.y/v.y) in (0.0-EPSILON)..(1.0+EPSILON))
            return a && b
        }
        return false
    }

    /**
     * @param line the line to check for intersection with
     */
    fun intersects(line : LineSegment2D) : Vec2D? {
        val r = vector()
        val s = line.vector()

        val d = r.x*s.y - r.y*s.x
        //We have a parallel line, this means an ambiguous intersection point
        //We do a check for which end points are within the intersection and return the first match.
        if(d == 0.0) {
            return if(inSegment(line.start)) {
                line.start
            } else if(inSegment(line.end)){
                line.end
            } else if(line.inSegment(start)) {
                start
            } else if(line.inSegment(end)) {
                end
            } else {
                null
            }
        }
        val u = ((line.start.x - start.x) * r.y - (line.start.y - start.y)*r.x)/d
        val t = ((line.start.x - start.x) * s.y - (line.start.y - start.y)*s.x)/d
        return if(u in (0.0- EPSILON) .. (1.0+ EPSILON) && t in (0.0- EPSILON) .. (1.0+ EPSILON)) {
            start+r*t
        } else {
            null
        }
    }

    override fun toString(): String {
        return "LineSegment2D(start=$start, end=$end)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LineSegment2D

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }
}

/**
 * Simple container for 2D vectors and points.
 */
class Vec2D(
    val x:Double,
    val y:Double)

{
    companion object {
        val ZERO = Vec2D(0.0,0.0);
        val LEFT = Vec2D(-1.0,0.0)
        val RIGHT = Vec2D(1.0,0.0)
        val UP = Vec2D(0.0,1.0)
        val DOWN = Vec2D(0.0,-1.0)
    }

    /**
     * Invert the vector
     */
    operator fun unaryMinus() : Vec2D {
        return Vec2D(-x,-y)
    }

    /**
     * Add two vectors
     */
    operator fun plus(v: Vec2D) : Vec2D {
        return Vec2D(v.x+x,v.y+y)
    }

    /**
     * Subtract vector from this
     */
    operator fun minus(v: Vec2D) : Vec2D {
        return Vec2D(x-v.x,y-v.y)
    }

    /**
     * Multiply vectors components with f
     * @param f
     */
    operator fun times(f:Double): Vec2D {
        return Vec2D(x*f,y*f)
    }

    /**
     * Divide vector components by f
     */
    operator fun div(f:Double) : Vec2D {
        return Vec2D(x/f,y/f)
    }


    fun unit() : Vec2D {
        return if(this == ZERO) {
            ZERO
        } else {
            this.div(length());
        }
    }

    /**
     * Construct a vector that is normal to this vector
     * unless it is the ZERO vector
     */
    fun normalVector() : Vec2D{
        return Vec2D(-y,x)
    }

    /**
     * Dot product with vec
     * @param vec
     */
    fun dot(vec : Vec2D) : Double {
        return this.x*vec.x + this.y*vec.y;
    }

    /**
     * Vector length (euclidian)
     */
    fun length() : Double {
        return sqrt(x*x+y*y);
    }

    /**
     * Equals can only be used for exact comparisons of binary double values.
     * In other words. Do not expect (A * B + C - C)/B to be equal to A.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2D

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    /**
     * Hashcode can only be used for exact comparisons of binary double values.
     * In other words. Do not expect (A * B + C - C)/B to be equal to A, and
     * have matching hashcode.
     */
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "($x,$y)"
    }


}