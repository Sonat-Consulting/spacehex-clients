package main

import (
	"math/rand"
	"testing"
)

func checkAllNotIntersects(t *testing.T, a, b LineSegment2D) {
	t.Helper()
	if a.Intersects(b) != nil {
		t.Errorf("Expected no intersection, but got intersection for %v, %v", a, b)
	}
	if a.Swap().Intersects(b) != nil {
		t.Errorf("Expected no intersection, but got intersection for %v, %v", a.Swap(), b)
	}
	if a.Intersects(b.Swap()) != nil {
		t.Errorf("Expected no intersection, but got intersection for %v, %v", a, b.Swap())
	}
	if a.Swap().Intersects(b.Swap()) != nil {
		t.Errorf("Expected no intersection, but got intersection for %v, %v", a.Swap(), b.Swap())
	}
}

func checkAllIntersects(t *testing.T, a, b LineSegment2D) {
	t.Helper()
	if a.Intersects(b) == nil {
		t.Errorf("Expected intersection, but got none for %v, %v", a, b)
	}
	if a.Swap().Intersects(b) == nil {
		t.Errorf("Expected intersection, but got none for %v, %v", a.Swap(), b)
	}
	if a.Intersects(b.Swap()) == nil {
		t.Errorf("Expected intersection, but got none for %v, %v", a, b.Swap())
	}
	if a.Swap().Intersects(b.Swap()) == nil {
		t.Errorf("Expected intersection, but got none for %v, %v", a.Swap(), b.Swap())
	}
}

func TestSegmentIntersect(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB := NewLineSegment2D(NewVec2D(-1.0, 1.0), NewVec2D(1.0, -1.0))
	pt := segmA.Intersects(segmB)
	checkAllIntersects(t, segmA, segmB)
	if pt.Sub(ZERO).Length() >= 0.01 {
		t.Errorf("Expected intersection point near origin, but got %v", pt)
	}
}

func TestSegmentNotIntersect(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB := NewLineSegment2D(NewVec2D(-2.0, -2.0), NewVec2D(-1.5, -1.5))
	checkAllNotIntersects(t, segmA, segmB)
}

func TestSegmentIntersectIfParallel(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB := NewLineSegment2D(NewVec2D(-2.0, -2.0), NewVec2D(-1.0, -1.0))
	checkAllIntersects(t, segmA, segmB)

	segmA = NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB = NewLineSegment2D(NewVec2D(-2.0, -2.0), NewVec2D(2.0, 2.0))
	checkAllIntersects(t, segmA, segmB)

	segmA = NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB = NewLineSegment2D(NewVec2D(-0.5, -0.5), NewVec2D(2.0, 2.0))
	checkAllIntersects(t, segmA, segmB)

	segmA = NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(1.0, 1.0))
	segmB = NewLineSegment2D(NewVec2D(-0.5, -0.5), NewVec2D(-0.5, -0.5))
	checkAllIntersects(t, segmA, segmB)
}

func TestSegmentIntersectInEndPoint(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, 1.0), NewVec2D(1.0, 1.0))
	segmB := NewLineSegment2D(NewVec2D(-2.0, -2.0), NewVec2D(1.0, 1.0))
	pt := segmA.Intersects(segmB)
	checkAllIntersects(t, segmA, segmB)
	if pt.Sub(NewVec2D(1.0, 1.0)).Length() >= 0.01 {
		t.Errorf("Expected intersection point near (1.0, 1.0), but got %v", pt)
	}
}

func TestSegmentIntersectInPoint(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-2.0, -2.0), NewVec2D(1.0, 1.0))
	segmB := NewLineSegment2D(NewVec2D(1.0, 1.0), NewVec2D(3.0, -1.0))

	pt := segmA.Intersects(segmB)
	checkAllIntersects(t, segmA, segmB)
	if pt.Sub(NewVec2D(1.0, 1.0)).Length() >= 0.01 {
		t.Errorf("Expected intersection point near (1.0, 1.0), but got %v", pt)
	}
}

func TestSegmentNoIntersectCase(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-341.0, -223.0), NewVec2D(-292.0, -194.0))
	segmB := NewLineSegment2D(NewVec2D(445.8586189801885, 246.7999999999996), NewVec2D(447.3242977416373, 243.5499999999996))
	checkAllNotIntersects(t, segmA, segmB)
}

func TestSegmentNormalVectorIntersectAll(t *testing.T) {
	for i := 0; i < 100; i++ {
		nX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		nY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		eX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		eY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		segmA := NewLineSegment2D(NewVec2D(nX, nY), NewVec2D(nX+eX, nY+eY))
		vec := segmA.Vector()
		norm := vec.NormalVector().Unit()
		segmB := NewLineSegment2D(
			segmA.Start.Add(segmA.End).Mul(0.5).Sub(norm.Mul(20.0)),
			segmA.Start.Add(segmA.End).Mul(0.5).Add(norm.Mul(20.0)),
		)
		checkAllIntersects(t, segmA, segmB)
	}
}

func TestSegmentIntersectAll(t *testing.T) {
	for i := 0; i < 100; i++ {
		nX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		nY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		eX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		eY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		segmA := NewLineSegment2D(NewVec2D(nX, nY), NewVec2D(nX+eX, nY+eY))

		vec := segmA.Vector()
		sign := RandomSign()
		normA := vec.NormalVector().Unit().Mul(sign * (RandomFloat64() * 100))
		segmB := NewLineSegment2D(
			segmA.Start.Add(segmA.Vector().Mul(RandomFloat64())),
			segmA.Start.Add(segmA.Vector().Mul(RandomFloat64())).Add(normA),
		)
		checkAllIntersects(t, segmA, segmB)
	}
}

func RandomSign() float64 {
	if RandomBool() {
		return -1.0
	}
	return 1.0
}

func RandomBool() bool {
	return rand.Float64() < 0.5
}

func RandomFloat64() float64 {
	return rand.Float64()
}

func TestSegmentParallelAndNoIntersect(t *testing.T) {
	for i := 0; i < 100; i++ {
		nX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		eX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		segmA := NewLineSegment2D(NewVec2D(nX, nX), NewVec2D(nX+eX, eX))
		norm := segmA.Vector().NormalVector().Mul(RandomSign() * (RandomFloat64() + 0.1) * 1000.0)

		vecDir := segmA.Vector().Mul((RandomFloat64() + 0.1) * 1000.0)
		segmB := NewLineSegment2D(segmA.Start.Add(norm).Add(vecDir), segmA.End.Add(norm).Add(vecDir))
		checkAllNotIntersects(t, segmA, segmB)
	}
}

func TestParallellIntersection(t *testing.T) {
	for i := 0; i < 100; i++ {
		nX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		nY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		eX := (RandomFloat64() + 0.1) * 10.0 * RandomSign()
		eY := (RandomFloat64() + 0.1) * 10.0 * RandomSign()

		segmA := NewLineSegment2D(NewVec2D(nX, nY), NewVec2D(eX, eY))
		vec := segmA.Vector()
		norm := vec.NormalVector().Unit()
		segmB := NewLineSegment2D(
			segmA.Start.Add(segmA.End).Mul(0.5).Sub(norm.Mul(20.0)),
			segmA.Start.Add(segmA.End).Mul(0.5).Add(norm.Mul(20.0)),
		)
		checkAllIntersects(t, segmA, segmB)
	}
}

func TestInSegmentSingle(t *testing.T) {
	segm2 := NewLineSegment2D(NewVec2D(-2.0, -1.0), NewVec2D(2.0, 1.0))

	pt := NewVec2D(-0.9423157439768692, -0.4711578719884346)
	if !segm2.InSegment(pt) {
		t.Errorf("%v not in segment", pt)
	}
}

func TestInSegment(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(2.0, 2.0))

	for i := 0; i < 100; i++ {
		n := (RandomFloat64() - 0.5) * 2
		pt := NewVec2D(n, n)
		if !segmA.InSegment(pt) {
			t.Errorf("%v not in segment", pt)
		}
	}

	segm2 := NewLineSegment2D(NewVec2D(-2.0, -1.0), NewVec2D(2.0, 1.0))

	for i := 0; i < 100; i++ {
		n := (RandomFloat64() - 0.5) * 2
		pt := NewVec2D(2*n, n)
		if !segm2.InSegment(pt) {
			t.Errorf("%v not in segment", pt)
		}
	}

	segm3 := NewLineSegment2D(NewVec2D(-2.0, 0.0), NewVec2D(2.0, 0.0))

	for i := 0; i < 100; i++ {
		n := (RandomFloat64() - 0.5) * 2
		pt := NewVec2D(2*n, 0.0)
		if !segm3.InSegment(pt) {
			t.Errorf("%v not in segment", pt)
		}
	}

	segm4 := NewLineSegment2D(NewVec2D(0.0, -2.0), NewVec2D(0.0, 2.0))

	for i := 0; i < 100; i++ {
		n := (RandomFloat64() - 0.5) * 2
		pt := NewVec2D(0.0, 2*n)
		if !segm4.InSegment(pt) {
			t.Errorf("%v not in segment", pt)
		}
	}
}

func TestCaseNotInSegment(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(2.0, 2.0))
	pt := NewVec2D(-9.829257132209216, -9.829257132209216)
	if segmA.InSegment(pt) {
		t.Errorf("%v in segment", pt)
	}

	pt2 := NewVec2D(9.829257132209216, 9.829257132209216)
	if segmA.InSegment(pt2) {
		t.Errorf("%v in segment", pt2)
	}

	pt3 := NewVec2D(2.02, 2.02)
	if segmA.InSegment(pt3) {
		t.Errorf("%v in segment", pt3)
	}

	pt4 := NewVec2D(-1.01, 1.01)
	if segmA.InSegment(pt4) {
		t.Errorf("%v in segment", pt4)
	}
}

func TestClosestPoint(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(2.0, 2.0))

	closest := segmA.ClosestPoint(NewVec2D(3.0, 3.0))
	expected := NewVec2D(2.0, 2.0)
	if closest.Sub(expected).Length() > 0.01 {
		t.Errorf("Closest point was %v, expected %v", closest, expected)
	}

	closest = segmA.ClosestPoint(NewVec2D(-2.0, -2.0))
	expected = NewVec2D(-1.0, -1.0)
	if closest.Sub(expected).Length() > 0.01 {
		t.Errorf("Closest point was %v, expected %v", closest, expected)
	}

	closest = segmA.ClosestPoint(NewVec2D(0.0, 0.0))
	expected = NewVec2D(0.0, 0.0)
	if closest.Sub(expected).Length() > 0.01 {
		t.Errorf("Closest point was %v, expected %v", closest, expected)
	}

	closest = segmA.ClosestPoint(NewVec2D(0.0, 0.0))
	expected = NewVec2D(0.0, 0.0)
	if closest.Sub(expected).Length() > 0.01 {
		t.Errorf("Closest point was %v, expected %v", closest, expected)
	}
}

func TestClosestPointCase(t *testing.T) {
	segmA := NewLineSegment2D(NewVec2D(-1.0, -1.0), NewVec2D(2.0, 2.0))

	for i := 0; i < 100; i++ {
		v := segmA.Vector()
		n := v.NormalVector().Mul(RandomSign())

		p := segmA.Start.Add(v.Mul(RandomFloat64())) // Pick a point along the line
		check := p.Add(n.Mul(100.0))                 // Move along the normal from that point

		closest := segmA.ClosestPoint(check)
		if closest.Sub(p).Length() > 0.01 {
			t.Errorf("Closest point was %v, expected %v for %v", closest, p, check)
		}
	}

	for i := 0; i < 100; i++ {
		v := segmA.Vector()
		n := v.NormalVector().Mul(RandomSign())

		p := segmA.Start.Add(v.Mul(-100.0 * RandomFloat64())) // Pick a point before start
		check := p.Add(n.Mul(100.0))                          // Move along the normal from that point

		closest := segmA.ClosestPoint(check)
		if closest.Sub(segmA.Start).Length() > 0.01 {
			t.Errorf("Closest point was %v, expected %v for %v", closest, segmA.Start, check)
		}
	}

	for i := 0; i < 100; i++ {
		v := segmA.Vector()
		n := v.NormalVector().Mul(RandomSign())

		p := segmA.Start.Add(v.Mul(1.0 + 100.0*RandomFloat64())) // Pick a point after end
		check := p.Add(n.Mul(100.0))                             // Move along the normal from that point

		closest := segmA.ClosestPoint(check)
		if closest.Sub(segmA.End).Length() > 0.01 {
			t.Errorf("Closest point was %v, expected %v for %v", closest, segmA.End, check)
		}
	}
}
