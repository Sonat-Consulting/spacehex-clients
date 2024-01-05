package no.sonat.game.geometry

import kotlin.*
import kotlin.math.*

/**
 *
 * Copyright (c) 04/01/2017, Jonas Waage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

val ZERO = Vec2D(0.0,0.0);
val UP = Vec2D(0.0,1.0);

/**
 * Clamp argument to min and max.
 */
fun range (arg:Double, min:Double, max:Double) : Double {
    return min(max(arg,min),max);
}

/**
 *
 */
fun isInRange(arg:Double, min:Double,max:Double) :Boolean {
    return arg >= min && arg <=max;
}

/**
 *
 */
data class InfiniteLine2D(val start : Vec2D, val dir : Vec2D) {

    init {
        if(dir == ZERO) throw IllegalArgumentException("Line can not have no direction");
    }

    fun intersects(line : InfiniteLine2D) : Vec2D? {
        val b = this.start;
        val a = line.start;
        val w = this.dir;
        val v = line.dir;

        val num = a.y*w.x - a.x*w.y - b.y*w.x + b.x*w.y;

        val denom = v.x*w.y - v.y*w.x;

        if(denom == 0.0) {
            return null; //Parallel stuff so threre is no intersection
        }

        val t = num/denom;
        return line.start + line.dir*t;
    }

}



/**
 *
 */
data class LineSegment2D(val start : Vec2D, val end : Vec2D) {

    fun vector() : Vec2D {
        return end - start;
    }

    fun horizontal() : Boolean {
        return direction().y == 0.0;
    }

    fun vertical() : Boolean {
        return direction().x == 0.0;
    }

    fun length() : Double {
        return (end-start).length() ;
    }

    fun direction() : Vec2D {
        return (end-start).normalize();
    }

    fun container() : Rect2D {
        val x = min(this.start.x,this.end.x);
        val y = min(this.start.y,this.end.y);
        return Rect2D(Vec2D(x,y),abs(this.start.x-this.end.x),abs(this.start.y-this.end.y))
    }

    fun intersects(line : LineSegment2D) : Vec2D? {
        val b = this.start
        val a = line.start
        val w = direction()
        val v = line.direction()

        val num = (a.y*w.x - a.x*w.y - b.y*w.x + b.x*w.y)

        val denom = (v.x*w.y - v.y*w.x)

        if(denom == 0.0) {
            return null; //Parallel stuff so threre is no intersection
        }

        val t = num/denom;
        val pos = line.start + line.direction()*t;

        if(this.container().containsPoint(pos) && line.container().containsPoint(pos)) {
            return pos
        }
        return null;
    }

}



data class LineSegment1D(val start:Double, val end:Double){

    init {
        if(end < start) {
            throw IllegalArgumentException("Cannot have negative segment");
        }
    }

    fun length() : Double {
        return abs(end-start);
    }

    fun intersection(seg : LineSegment1D) : LineSegment1D? {
        if(start < seg.start) {
            if(end < seg.start) {
                return null;
            }
            else {
                return LineSegment1D(seg.start,min(end,seg.end));
            }
        }
        else {
            if(seg.end < start) {
                return null;
            }
            else {
                return LineSegment1D(start, min(end,seg.end));
            }
        }
    }
}

data class Rect2D(val ul: Vec2D, val width:Double, val height:Double) {

    val bl get() = ul+ Vec2D(0.0,height);
    val ur get() = ul+ Vec2D(width,0.0);
    val br get() = ul+ Vec2D(width,height);


    fun moveTo(pos: Vec2D) : Rect2D {
        return Rect2D(pos,width,height);
    }

    fun points() : Collection<Vec2D> {
        return listOf(
            ul,
            bl,
            ur,
            br);
    }

    fun intersections(line : InfiniteLine2D) : List<Vec2D> {
        val arr = setOf(
            line.intersects(InfiniteLine2D(ul, Vec2D(1.0,0.0))),
            line.intersects(InfiniteLine2D(ul, Vec2D(0.0,1.0))),
            line.intersects(InfiniteLine2D(ul+ Vec2D(width,height), Vec2D(1.0,0.0))),
            line.intersects(InfiniteLine2D(ul+ Vec2D(width,height), Vec2D(0.0,1.0)))
        );
        val out : List<Vec2D> = arr.filter { i->i is Vec2D }.map { i-> i as Vec2D };
        val ret = out.filter { i-> this.containsPoint(i) };
        return ret;
    }


    fun getLineSegments() : Collection<LineSegment2D> {
        return listOf(
            LineSegment2D(ul,ul + Vec2D(width,0.0)),
            LineSegment2D(ul,ul + Vec2D(0.0,height)),
            LineSegment2D(ul+ Vec2D(0.0,height),ul+ Vec2D(width,height)),
            LineSegment2D(ul+ Vec2D(width,0.0),ul+ Vec2D(width,height))
        );
    }


    fun containingRect(other: Rect2D) : Rect2D {
        val x = min(this.ul.x, other.ul.x);
        val y = min(this.ul.y, other.ul.y);
        val mx = max(this.br.x, other.br.x);
        val my = max(this.br.y, other.br.y);
        return Rect2D(Vec2D(x,y),mx-x,my-y);
    }

    fun intersection(rect : Rect2D) : Rect2D? {
        val x1 = LineSegment1D(ul.x,ul.x+width);
        val x2 = LineSegment1D(rect.ul.x,rect.ul.x+rect.width);
        val segX = x1.intersection(x2);

        val y1 = LineSegment1D(ul.y,ul.y+height);
        val y2 = LineSegment1D(rect.ul.y,rect.ul.y + rect.height);
        val segY = y1.intersection(y2);

        if(segX != null && segY !=null) {
            return Rect2D(Vec2D(segX.start,segY.start),segX.length(),segY.length());
        }
        return null;
    }


    private fun shrinkLine(shrink:Double,start:Double,lgt:Double) : Vec2D {
        return if(lgt <= 0) {
            Vec2D(start,0.0);
        }
        else {
            val w = lgt - shrink*2;
            val x = if(w < 0) { start + lgt/2 } else {start + shrink};
            return Vec2D(x,w);
        }
    }

    fun shrink(shrink : Double) : Rect2D {
        val x = shrinkLine(shrink,ul.x,width);
        val y = shrinkLine(shrink,ul.y,height);
        return Rect2D(Vec2D(x.x,y.x),x.y,y.y);
    }

    fun shrinkX(shrink : Double) : Rect2D {
        val x = shrinkLine(shrink,ul.x,width);
        return Rect2D(Vec2D(x.x,ul.y),x.y,height);
    }

    fun shrinkY(shrink : Double) : Rect2D {
        val y = shrinkLine(shrink,ul.y,height);
        return Rect2D(Vec2D(ul.x,y.x),width,y.y);
    }

    fun intersects(rect : Rect2D) : Boolean {
        val isectA = this.points().fold(false,{ a,b -> a.or(rect.containsPoint(b)); });
        val isectB = rect.points().fold(false,{ a,b -> a.or(this.containsPoint(b)); })
        return isectA || isectB;
    }

    fun center() : Vec2D {
        return ul + Vec2D(width/2.0,height/2.0);
    }

    fun containsPoint(vec : Vec2D) :Boolean {
        return isInRange(vec.x,ul.x,ul.x+width) && isInRange(vec.y,ul.y,ul.y+height);
    }

    private fun clampToRect(vec : Vec2D) : Vec2D {
        return Vec2D(range(vec.x,ul.x,ul.x+width), range(vec.y,ul.y,ul.y+height));
    }

    private fun closestPoint(ps : Double, p1 :Double, p2:Double) : Double {
        return if(abs(ps-p1) <= abs(ps-p2)) {
            p1;
        } else {
            p2;
        }
    }

    fun closestEdgePoint( point: Vec2D) : Vec2D {
        val a = closestPoint(point.x,ul.x,ul.x+width);
        val b = closestPoint(point.y,ul.y,ul.y+height);

        val adX = abs(point.x-a);
        val adY = abs(point.y-b);

        return if(adX <= adY) {
            clampToRect(Vec2D(a,point.y));
        } else {
            clampToRect(Vec2D(point.x,b));
        }
    }

    fun move(vec: Vec2D) : Rect2D {
        return Rect2D(ul+vec,width,height);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rect2D

        if(width.isNaN() || height.isNaN() || other.width.isNaN() || other.height.isNaN()) return false

        if (ul != other.ul) return false
        if (abs(width - other.width) > Double.MIN_VALUE) return false
        if (abs(height - other.height) > Double.MIN_VALUE) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ul.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        return result
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
        return Vec2D(x,y);
    }

    operator fun unaryMinus() : Vec2D {
        return Vec2D(-x,-y);
    }

    operator fun plus(v: Vec2D) : Vec2D {
        return Vec2D(v.x+x,v.y+y);
    }

    operator fun minus(v: Vec2D) : Vec2D {
        return Vec2D(x-v.x,y-v.y);
    }

    operator fun times(vec : Vec2D) : Vec2D {
        return Vec2D(vec.x*x,vec.y*y);
    }

    operator fun times(f:Double): Vec2D {
        return Vec2D(x*f,y*f);
    }

    operator fun div(f:Double) : Vec2D {
        return Vec2D(x/f,y/f);
    }

    fun flip() : Vec2D {
        return Vec2D(y,x);
    }

    fun createOrthogonalVector() : Vec2D {
        return Vec2D(-x,y).flip();
    }

    fun signX() : Double {
        if(this.x > 0) return 1.0;
        if(this.x < 0) return 1.0;
        return 0.0;
    }

    fun signY() : Double {
        if(this.y > 0) return 1.0;
        if(this.y < 0) return 1.0;
        return 0.0;
    }

    fun normalize() : Vec2D {
        if(this == ZERO) {
            return ZERO;
        }
        else {
            return this.div(length());
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