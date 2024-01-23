import unittest
import random

from simplegeometry import LineSegment2D, Vec2D


class TestGeometry(unittest.TestCase):

    def check_all_not_intersects(self, a, b):
        self.assertIsNone(a.intersects(b))
        self.assertIsNone(a.swap().intersects(b))
        self.assertIsNone(a.intersects(b.swap()))
        self.assertIsNone(a.swap().intersects(b.swap()))

    def check_all_intersects(self, a, b):
        self.assertIsNotNone(a.intersects(b))
        self.assertIsNotNone(a.swap().intersects(b))
        self.assertIsNotNone(a.intersects(b.swap()))
        self.assertIsNotNone(a.swap().intersects(b.swap()))

    def test_segment_intersect(self):
        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-1.0, 1.0), Vec2D(1.0, -1.0))
        pt = segmA.intersects(segmB)
        self.check_all_intersects(segmA, segmB)
        self.assertTrue((pt - Vec2D.ZERO).length() < 0.01)

    def test_segment_not_intersect(self):
        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(-1.5, -1.5))
        self.check_all_not_intersects(segmA, segmB)

    def test_segment_intersect_if_parallel(self):
        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(-1.0, -1.0))
        self.check_all_intersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(2.0, 2.0))
        self.check_all_intersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-0.5, -0.5), Vec2D(2.0, 2.0))
        self.check_all_intersects(segmA, segmB)

        segmA = LineSegment2D(Vec2D(-1.0, -1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-0.5, -0.5), Vec2D(-0.5, -0.5))
        self.check_all_intersects(segmA, segmB)

    def test_segment_intersect_in_end_point(self):
        segmA = LineSegment2D(Vec2D(-1.0, 1.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(1.0, 1.0))
        pt = segmA.intersects(segmB)
        self.check_all_intersects(segmA, segmB)
        self.assertTrue((pt - Vec2D(1.0, 1.0)).length() < 0.01)

    def test_segment_intersect_in_point(self):
        segmA = LineSegment2D(Vec2D(-2.0, -2.0), Vec2D(1.0, 1.0))
        segmB = LineSegment2D(Vec2D(1.0, 1.0), Vec2D(3.0, -1.0))
        pt = segmA.intersects(segmB)
        self.check_all_intersects(segmA, segmB)
        self.assertTrue((pt - Vec2D(1.0, 1.0)).length() < 0.01)

    def test_segment_no_intersect_case(self):
        segmA = LineSegment2D(start=Vec2D(x=-341.0, y=-223.0), end=Vec2D(x=-292.0, y=-194.0))
        segmB = LineSegment2D(start=Vec2D(x=445.8586189801885, y=246.7999999999996),
                              end=Vec2D(x=447.3242977416373, y=243.5499999999996))
        self.check_all_not_intersects(segmA, segmB)

    def test_segment_normal_vector_intersect_all(self):
        for _ in range(100):
            n_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            n_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            e_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            e_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            segmA = LineSegment2D(start=Vec2D(x=n_x, y=n_y), end=Vec2D(x=n_x + e_x, y=n_y + e_y))
            vec = segmA.vector()
            norm = Vec2D(vec.y, vec.x).unit()
            segmB = LineSegment2D(
                start=(segmA.start + segmA.end) / 2.0 - norm * 20.0,
                end=(segmA.start + segmA.end) / 2.0 + norm * 20.0
            )
            self.check_all_intersects(segmA, segmB)

    def test_segment_intersect_all(self):
        for _ in range(100):
            n_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            n_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            e_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            e_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            segmA = LineSegment2D(start=Vec2D(x=n_x, y=n_y), end=Vec2D(x=n_x + e_x, y=n_y + e_y))

            vec = segmA.vector()
            sign = self.random_sign()
            norm_a = Vec2D(vec.y, vec.x).unit() * sign * (random.random() * 100)
            norm_b = Vec2D(vec.y, vec.x).unit() * -sign * (random.random() * 100)
            segmB = LineSegment2D(
                start=(segmA.start + segmA.vector() * random.random()) + norm_a,
                end=(segmA.start + segmA.vector() * random.random()) + norm_b
            )
            self.check_all_intersects(segmA, segmB)

    def random_sign(self):
        return -1.0 if random.choice([True, False]) else 1.0

    def test_segment_parallel_and_no_intersect(self):
        for _ in range(100):
            n_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            e_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            segmA = LineSegment2D(start=Vec2D(x=n_x, y=n_x), end=Vec2D(x=n_x + e_x, y=e_x))
            norm = segmA.vector().normal_vector() * self.random_sign() * (random.random() + 0.1) * 1000.0

            vec_dir = segmA.vector() * (random.random() + 0.1) * 1000.0
            segmB = LineSegment2D(start=Vec2D(x=n_x, y=n_x) + norm + vec_dir,
                                  end=Vec2D(x=n_x + e_x, y=e_x) + norm + vec_dir)
            self.check_all_not_intersects(segmA, segmB)

    def test_parallel_intersection(self):
        for _ in range(100):
            n_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            n_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            e_x = (random.random() + 0.1) * 10.0 * self.random_sign()
            e_y = (random.random() + 0.1) * 10.0 * self.random_sign()

            segmA = LineSegment2D(start=Vec2D(x=n_x, y=n_y), end=Vec2D(x=e_x, y=e_y))
            vec = segmA.vector()
            norm = Vec2D(vec.y, vec.x).unit()
            segmB = LineSegment2D(
                start=(segmA.start + segmA.end) / 2.0 - norm * 20.0,
                end=(segmA.start + segmA.end) / 2.0 + norm * 20.0
            )
            self.check_all_intersects(segmA, segmB)

    def test_in_segment_single(self):
        segm2 = LineSegment2D(start=Vec2D(x=-2.0, y=-1.0), end=Vec2D(x=2.0, y=1.0))
        pt = Vec2D(-0.9423157439768692, -0.4711578719884346)
        self.assertTrue(segm2.in_segment(pt), f"{pt} not in segment")

    def test_in_segment(self):
        segmA = LineSegment2D(start=Vec2D(x=-1.0, y=-1.0), end=Vec2D(x=2.0, y=2.0))

        for _ in range(100):
            n = (random.random() - 0.5) * 2
            pt = Vec2D(n, n)
            self.assertTrue(segmA.in_segment(pt), f"{pt} not in segment")

        segm2 = LineSegment2D(start=Vec2D(x=-2.0, y=-1.0), end=Vec2D(x=2.0, y=1.0))

        for _ in range(100):
            n = (random.random() - 0.5) * 2
            pt = Vec2D(2 * n, n)
            self.assertTrue(segm2.in_segment(pt), f"{pt} not in segment")

        segm3 = LineSegment2D(start=Vec2D(x=-2.0, y=0.0), end=Vec2D(x=2.0, y=0.0))

        for _ in range(100):
            n = (random.random() - 0.5) * 2
            pt = Vec2D(2 * n, 0.0)
            self.assertTrue(segm3.in_segment(pt), f"{pt} not in segment")

        segm4 = LineSegment2D(start=Vec2D(x=0.0, y=-2.0), end=Vec2D(x=0.0, y=2.0))

        for _ in range(100):
            n = (random.random() - 0.5) * 2
            pt = Vec2D(0.0, 2 * n)
            self.assertTrue(segm4.in_segment(pt), f"{pt} not in segment")

    def test_case_not_in_segment(self):
        segmA = LineSegment2D(start=Vec2D(x=-1.0, y=-1.0), end=Vec2D(x=2.0, y=2.0))
        pt = Vec2D(-9.829257132209216, -9.829257132209216)
        self.assertFalse( f"{pt} in segment",segmA.in_segment(pt))

        pt2 = Vec2D(9.829257132209216, 9.829257132209216)
        self.assertFalse( f"{pt2} in segment",segmA.in_segment(pt2))

        pt3 = Vec2D(2.02, 2.02)
        self.assertFalse( f"{pt3} in segment",segmA.in_segment(pt3))

        pt4 = Vec2D(-1.01, 1.01)
        self.assertFalse( f"{pt3} in segment",segmA.in_segment(pt4))

    def test_case_not_in_segment(self):
        segmA = LineSegment2D(Vec2D(x=-1.0, y=-1.0), Vec2D(x=2.0, y=2.0))
        pt = Vec2D(-9.829257132209216, -9.829257132209216)
        self.assertFalse( segmA.in_segment(pt), f"{pt} in segment")

        pt2 = Vec2D(9.829257132209216, 9.829257132209216)
        self.assertFalse( segmA.in_segment(pt2), f"{pt2} in segment")

        pt3 = Vec2D(2.02, 2.02)
        self.assertFalse( segmA.in_segment(pt3), f"{pt3} in segment")

        pt4 = Vec2D(-1.01, 1.01)
        self.assertFalse(segmA.in_segment(pt4), f"{pt4} in segment")

    def test_not_in_segment(self):
        segmA = LineSegment2D(Vec2D(x=-1.0, y=-1.0), Vec2D(x=2.0, y=2.0))

        for _ in range(100):
            n = random.random() + 0.1
            pt = Vec2D(n, n + 0.25)
            self.assertFalse( segmA.in_segment(pt), f"{pt} in segment")

        for _ in range(100):
            n = random.random()
            pt = Vec2D(n - 10, n - 10)
            self.assertFalse( segmA.in_segment(pt), f"{pt} in segment")

        segm2 = LineSegment2D(Vec2D(x=-2.0, y=-1.0), Vec2D(x=2.0, y=1.0))

        for _ in range(100):
            n = (random.random() - 0.5) * 2
            pt = Vec2D(2 * n, n + 0.3)
            self.assertFalse( segm2.in_segment(pt), f"{pt} in segment")

    def test_closest_point(self):
        segmA = LineSegment2D(Vec2D(x=-1.0, y=-1.0), Vec2D(x=2.0, y=2.0))

        closest = segmA.closest_point(Vec2D(3.0, 3.0))
        self.assertTrue((closest - Vec2D(2.0, 2.0)).length() < 0.01, f"Closest point was {closest}")

        closest = segmA.closest_point(Vec2D(-2.0, -2.0))
        self.assertTrue( (closest - Vec2D(-1.0, -1.0)).length() < 0.01,f"Closest point was {closest}")

        closest = segmA.closest_point(Vec2D(0.0, 0.0))
        self.assertTrue( (closest - Vec2D(0.0, 0.0)).length() < 0.01, f"Closest point was {closest}")

        closest = segmA.closest_point(Vec2D(0.0, 0.0))
        self.assertTrue( (closest - Vec2D(0.0, 0.0)).length() < 0.01, f"Closest point was {closest}")

    def test_closest_point_case(self):
        segmA = LineSegment2D(Vec2D(x=-1.0, y=-1.0), Vec2D(x=2.0, y=2.0))

        for _ in range(100):
            v = segmA.vector()
            n = v.normal_vector() * self.random_sign()

            p = segmA.start + v * random.random()  # Pick a point along the line
            check = p + n * 100.0  # Move along the normal from that point

            closest = segmA.closest_point(check)
            self.assertTrue(
                (closest - p).length() < 0.01, f"Closest point was {closest} expected {p} for {check}"
            )

        for _ in range(100):
            v = segmA.vector()
            n = v.normal_vector() * self.random_sign()

            p = segmA.start + v * (random.random() * -100)  # Pick a point before start
            check = p + n * 100.0  # Move along the normal from that point

            closest = segmA.closest_point(check)
            self.assertTrue(
                (closest - segmA.start).length() < 0.01,f"Closest point was {closest} expected {p} for {check}"
            )

        for _ in range(100):
            v = segmA.vector()
            n = v.normal_vector() * self.random_sign()

            p = segmA.start + v * (1 + random.random() * 100)  # Pick a point after end
            check = p + n * 100.0  # Move along the normal from that point

            closest = segmA.closest_point(check)
            self.assertTrue(
                (closest - segmA.end).length() < 0.01,f"Closest point was {closest} expected {p} for {check}"
            )

    def random_sign(self):
        return -1.0 if random.choice([True, False]) else 1.0
