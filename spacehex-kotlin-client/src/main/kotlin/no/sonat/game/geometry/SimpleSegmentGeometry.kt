package no.sonat.game.geometry

import kotlin.*
import kotlin.math.*

/**
 * used for number comparisons
 */
const val EPSILON = 0.0000001

/**
 * Segment between points start and end
 * @param start start point
 * @param end end point
 */
class LineSegment2D(val start : Vec2D, val end : Vec2D) {

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
        return (end-start).normalize()
    }

    /**
     * @param line the line to check for intersection with
     */
    fun intersects(line : LineSegment2D) : Vec2D? {
        val b = this.start
        val a = line.start
        val w = direction()
        val v = line.direction()

        val num = (a.y*w.x - a.x*w.y - b.y*w.x + b.x*w.y)

        val denom = (v.x*w.y - v.y*w.x)

        if(denom == 0.0) {
            //This can only happen if we enter the surface parallel from the side
            //If that is done, the collision should be picked up by the surface segments on each side.
            return null
        }

        val t = num/denom;
        val pos = line.start + line.direction()*t

        val l1maxX = max(line.start.x,line.end.x) + EPSILON
        val l1minX = min(line.start.x,line.end.x) - EPSILON
        val l1maxY = max(line.start.y,line.end.y) + EPSILON
        val l1minY = min(line.start.y,line.end.y) - EPSILON

        val l2maxX = max(this.start.x,this.end.x) + EPSILON
        val l2minX = min(this.start.x,this.end.x) - EPSILON
        val l2maxY = max(this.start.y,this.end.y) + EPSILON
        val l2minY = min(this.start.y,this.end.y) - EPSILON

        if(
            pos.x in l1minX .. l1maxX && pos.y in l1minY .. l1maxY &&
            pos.x in l2minX .. l2maxX && pos.y in l2minY .. l2maxY ) {
            return pos
        }
        return null
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

    operator fun unaryPlus() : Vec2D {
        return Vec2D(x,y)
    }

    operator fun unaryMinus() : Vec2D {
        return Vec2D(-x,-y)
    }

    operator fun plus(v: Vec2D) : Vec2D {
        return Vec2D(v.x+x,v.y+y)
    }

    operator fun minus(v: Vec2D) : Vec2D {
        return Vec2D(x-v.x,y-v.y)
    }

    operator fun times(f:Double): Vec2D {
        return Vec2D(x*f,y*f)
    }

    operator fun div(f:Double) : Vec2D {
        return Vec2D(x/f,y/f)
    }

    fun normalize() : Vec2D {
        return if(this == ZERO) {
            ZERO
        } else {
            this.div(length());
        }
    }

    /**
     * Dot product with vec
     * @param vec
     */
    fun dot(vec : Vec2D) : Double {
        return this.x*vec.x + this.y*vec.y;
    }

    /**
     * @param vec vector to calculate angle to
     */
    fun angleTo(vec : Vec2D):Double {
        val lgt = this.length();
        val oLgt = vec.length();
        if(lgt == 0.0 || oLgt == 0.0) {
            return 0.0;
        }
        return acos(this.dot(vec)/(lgt*oLgt));
    }

    /**
     * Vector length (euclidian)
     */
    fun length() : Double {
        return sqrt(x*x+y*y);
    }

    /**
     * This vector projected onto a target vector
     */
    fun projectOnto(target: Vec2D): Vec2D {
        val dotTrg = target.dot(target);
        if(dotTrg == 0.0) {
            return ZERO;
        }
        val dot = this.dot(target);
        return target.times(dot/dotTrg);
    }

}