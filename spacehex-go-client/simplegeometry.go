package main

import (
	"math"
)

const EPSILON = 0.0000001

func (v Vec2D) unaryPlus() Vec2D {
	return Vec2D{v.X, v.Y}
}

func (v Vec2D) unaryMinus() Vec2D {
	return Vec2D{-v.X, -v.Y}
}

func (v Vec2D) plus(other Vec2D) Vec2D {
	return Vec2D{v.X + other.X, v.Y + other.Y}
}

func (v Vec2D) minus(other Vec2D) Vec2D {
	return Vec2D{v.X - other.X, v.Y - other.Y}
}

func (v Vec2D) times(f float64) Vec2D {
	return Vec2D{v.X * f, v.Y * f}
}

func (v Vec2D) div(f float64) Vec2D {
	return Vec2D{v.X / f, v.Y / f}
}

func (v Vec2D) length() float64 {
	return math.Sqrt(v.X*v.X + v.Y*v.Y)
}

func (v Vec2D) normalize() Vec2D {
	if v.length() == 0.0 {
		return Vec2D{}
	}
	return v.div(v.length())
}

func (v Vec2D) dot(other Vec2D) float64 {
	return v.X*other.X + v.Y*other.Y
}

func (v Vec2D) angleTo(other Vec2D) float64 {
	lgt := v.length()
	oLgt := other.length()
	if lgt == 0.0 || oLgt == 0.0 {
		return 0.0
	}
	return math.Acos(v.dot(other) / (lgt * oLgt))
}

func (v Vec2D) projectOnto(target Vec2D) Vec2D {
	dotTrg := target.dot(target)
	if dotTrg == 0.0 {
		return Vec2D{}
	}
	dot := v.dot(target)
	return target.times(dot / dotTrg)
}

func NewVec2D(x, y float64) Vec2D {
	return Vec2D{x, y}
}

func (ls LineSegment2D) vector() Vec2D {
	return ls.End.minus(ls.Start)
}

func (ls LineSegment2D) length() float64 {
	return ls.vector().length()
}

func (ls LineSegment2D) direction() Vec2D {
	return ls.vector().normalize()
}

func (ls LineSegment2D) intersects(line LineSegment2D) Vec2D {
	b := ls.Start
	a := line.Start
	w := ls.direction()
	v := line.direction()

	num := (a.Y*w.X - a.X*w.Y - b.Y*w.X + b.X*w.Y)
	denom := (v.X*w.Y - v.Y*w.X)

	if denom == 0.0 {
		return Vec2D{}
	}

	t := num / denom
	pos := line.Start.plus(v.times(t))

	l1maxX := math.Max(line.Start.X, line.End.X) + EPSILON
	l1minX := math.Min(line.Start.X, line.End.X) - EPSILON
	l1maxY := math.Max(line.Start.Y, line.End.Y) + EPSILON
	l1minY := math.Min(line.Start.Y, line.End.Y) - EPSILON

	l2maxX := math.Max(ls.Start.X, ls.End.X) + EPSILON
	l2minX := math.Min(ls.Start.X, ls.End.X) - EPSILON
	l2maxY := math.Max(ls.Start.Y, ls.End.Y) + EPSILON
	l2minY := math.Min(ls.Start.Y, ls.End.Y) - EPSILON

	if pos.X > l1minX && pos.X < l1maxX &&
		pos.Y > l1minY && pos.Y < l1maxY &&
		pos.X > l2minX && pos.X < l2maxX &&
		pos.Y > l2minY && pos.Y < l2maxY {
		return pos
	}
	return Vec2D{}
}

func NewLineSegment2D(start, end Vec2D) LineSegment2D {
	return LineSegment2D{start, end}
}
