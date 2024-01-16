package no.sonat.game.geometry

import kotlin.*
import kotlin.math.*

/**
 * Segments
 */
data class LineSegment2D(val start : Vec2D, val end : Vec2D) {

    fun vector() : Vec2D {
        return end - start;
    }

    fun length() : Double {
        return (end-start).length()
    }

    fun direction() : Vec2D {
        return (end-start).normalize()
    }

    fun intersects(line : LineSegment2D) : Vec2D? {
        val b = this.start
        val a = line.start
        val w = direction()
        val v = line.direction()

        val num = (a.y*w.x - a.x*w.y - b.y*w.x + b.x*w.y)

        val denom = (v.x*w.y - v.y*w.x)

        if(denom == 0.0) {
            //Parallel stuff so there is no intersection, avoid div by zero
            //Possibly use a smaller double comparison
            return null
        }

        val t = num/denom;
        val pos = line.start + line.direction()*t

        val maxX = max(line.start.x,line.end.x)
        val minX = min(line.start.x,line.end.x)
        val maxY = max(line.start.y,line.end.y)
        val minY = min(line.start.y,line.end.y)

        if(pos.x in minX .. maxX && pos.y in minY .. maxY) {
            return pos
        }
        return null;
    }

}


data class Vec2D(
    val x:Double,
    val y:Double)

{
    companion object {
        val ZERO = Vec2D(0.0,0.0);
        val LEFT = Vec2D(-1.0,0.0)
        val RIGHT = Vec2D(1.0,0.0)
        val UP = Vec2D(0.0,-1.0)
        val DOWN = Vec2D(0.0,1.0)
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

    operator fun times(vec : Vec2D) : Vec2D {
        return Vec2D(vec.x*x,vec.y*y)
    }

    operator fun times(f:Double): Vec2D {
        return Vec2D(x*f,y*f)
    }

    operator fun div(f:Double) : Vec2D {
        return Vec2D(x/f,y/f)
    }

    fun flip() : Vec2D {
        return Vec2D(y,x)
    }

    fun createOrthogonalVector() : Vec2D {
        return Vec2D(-x,y).flip()
    }

    fun normalize() : Vec2D {
        return if(this == ZERO) {
            ZERO
        } else {
            this.div(length());
        }
    }

    fun dot(vec : Vec2D) : Double {
        return this.x*vec.x + this.y*vec.y;
    }

    fun angleTo(vec : Vec2D):Double {
        val lgt = this.length();
        val oLgt = vec.length();
        if(lgt == 0.0 || oLgt == 0.0) {
            return 0.0;
        }
        return acos(this.dot(vec)/(lgt*oLgt));
    }

    fun length() : Double {
        return sqrt(x*x+y*y);
    }

    fun projectOnto(target: Vec2D): Vec2D {
        val dotTrg = target.dot(target);
        if(dotTrg == 0.0) {
            return ZERO;
        }
        val dot = this.dot(target);

        return target.times(dot/dotTrg);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2D

        if(x.isNaN() || y.isNaN() || other.x.isNaN() || other.y.isNaN()) return false

        if (abs(x-other.x) > Double.MIN_VALUE) return false
        if (abs(y-other.y) > Double.MIN_VALUE) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }


}