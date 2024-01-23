package main

import (
	"fmt"
	"math"
)

// EPSILON is used for FP number comparisons
// So we can control accuracy outside of pure FP accuracy.
const EPSILON = 0.0000001

// ZERO, LEFT, RIGHT, UP, and DOWN are predefined Vec2D instances.
var (
	ZERO  = Vec2D{0.0, 0.0}
	LEFT  = Vec2D{-1.0, 0.0}
	RIGHT = Vec2D{1.0, 0.0}
	UP    = Vec2D{0.0, 1.0}
	DOWN  = Vec2D{0.0, -1.0}
)

// NewVec2D creates a new Vec2D with the given x and y coordinates.
func NewVec2D(x, y float64) Vec2D {
	return Vec2D{x, y}
}

// Neg returns the negation of the vector.
func (v Vec2D) Neg() Vec2D {
	return Vec2D{-v.X, -v.Y}
}

// Add returns the sum of two vectors.
func (v Vec2D) Add(other Vec2D) Vec2D {
	return Vec2D{v.X + other.X, v.Y + other.Y}
}

// Sub returns the difference between two vectors.
func (v Vec2D) Sub(other Vec2D) Vec2D {
	return Vec2D{v.X - other.X, v.Y - other.Y}
}

// Mul returns the result of scaling the vector by a scalar.
func (v Vec2D) Mul(scalar float64) Vec2D {
	return Vec2D{v.X * scalar, v.Y * scalar}
}

// Div returns the result of dividing the vector by a scalar.
func (v Vec2D) Div(scalar float64) Vec2D {
	return Vec2D{v.X / scalar, v.Y / scalar}
}

// Unit returns a vector of length 1 with the same direction as this vector.
func (v Vec2D) Unit() Vec2D {
	length := v.Length()
	if length == 0.0 {
		return ZERO
	}
	return v.Div(length)
}

// NormalVector returns a vector that is normal to this vector.
func (v Vec2D) NormalVector() Vec2D {
	return Vec2D{-v.Y, v.X}
}

// Dot computes the dot product of two vectors.
func (v Vec2D) Dot(other Vec2D) float64 {
	return v.X*other.X + v.Y*other.Y
}

// Length returns the length of the vector.
func (v Vec2D) Length() float64 {
	return math.Sqrt(v.X*v.X + v.Y*v.Y)
}

// Equals checks if two vectors are equal.
func (v Vec2D) Equals(other Vec2D) bool {
	return math.Abs(v.X-other.X) < EPSILON && math.Abs(v.Y-other.Y) < EPSILON
}

// Hash computes the hash value for the vector.
func (v Vec2D) Hash() int {
	return int((math.Float64bits(v.X) * 73856093) ^ (math.Float64bits(v.Y) * 19349663))
}

// String returns a string representation of the vector.
func (v Vec2D) String() string {
	return fmt.Sprintf("(%v,%v)", v.X, v.Y)
}

// NewLineSegment2D creates a new LineSegment2D with the given start and end points.
func NewLineSegment2D(start, end Vec2D) LineSegment2D {
	return LineSegment2D{start, end}
}

// Swap returns a new LineSegment2D with the start and end points swapped.
func (l LineSegment2D) Swap() LineSegment2D {
	return LineSegment2D{l.End, l.Start}
}

// Vector returns the vector from start to end.
func (l LineSegment2D) Vector() Vec2D {
	return l.End.Sub(l.Start)
}

// Length returns the length of the line segment.
func (l LineSegment2D) Length() float64 {
	return l.Vector().Length()
}

// Direction returns the direction of the line segment.
func (l LineSegment2D) Direction() Vec2D {
	return l.Vector().Unit()
}

// ClosestPoint computes the closest point on this line segment to the given point.
func (l LineSegment2D) ClosestPoint(pt Vec2D) Vec2D {
	r := l.Vector()

	t := ((pt.X*r.X - l.Start.X*r.X) + (pt.Y*r.Y - l.Start.Y*r.Y)) / (r.Y*r.X + r.X*r.X)
	ptOnSegm := l.Start.Add(r.Mul(t))

	if l.InSegment(ptOnSegm) {
		return ptOnSegm
	}

	a := l.Start.Sub(ptOnSegm).Length()
	b := l.End.Sub(ptOnSegm).Length()

	if a < b {
		return l.Start
	}
	return l.End
}

// InSegment checks if the given point is on the line segment.
func (l LineSegment2D) InSegment(pt Vec2D) bool {
	v := l.Vector()
	t := pt.Sub(l.Start)

	if math.Abs(t.Y*v.X-t.X*v.Y) < EPSILON {
		a := (t.X == 0.0 || (0.0-EPSILON) <= (t.X/v.X) && (t.X/v.X) <= (1.0+EPSILON))
		b := (t.Y == 0.0 || (0.0-EPSILON) <= (t.Y/v.Y) && (t.Y/v.Y) <= (1.0+EPSILON))
		return a && b
	}

	return false
}

// Intersects computes the intersection point if the given line segment intersects this line segment.
// Returns nil if there is no intersection.
func (l LineSegment2D) Intersects(line LineSegment2D) *Vec2D {
	r := l.Vector()
	s := line.Vector()
	d := r.X*s.Y - r.Y*s.X

	if d == 0.0 {
		if l.InSegment(line.Start) {
			return &line.Start
		} else if l.InSegment(line.End) {
			return &line.End
		} else if line.InSegment(l.Start) {
			return &l.Start
		} else if line.InSegment(l.End) {
			return &l.End
		} else {
			return nil
		}
	}

	u := ((line.Start.X-l.Start.X)*r.Y - (line.Start.Y-l.Start.Y)*r.X) / d
	t := ((line.Start.X-l.Start.X)*s.Y - (line.Start.Y-l.Start.Y)*s.X) / d

	if 0.0-EPSILON <= u && u <= 1.0+EPSILON && 0.0-EPSILON <= t && t <= 1.0+EPSILON {
		intersectPoint := l.Start.Add(r.Mul(t))
		return &intersectPoint
	}

	return nil
}
