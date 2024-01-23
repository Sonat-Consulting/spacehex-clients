import math

# Used for FP number comparisons
# So we can control accuracy outside of pure FP accuracy.
EPSILON = 0.0000001


class Vec2D:
    ZERO = None
    LEFT = None
    RIGHT = None
    UP = None
    DOWN = None

    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __neg__(self):
        return Vec2D(-self.x, -self.y)

    def __add__(self, other):
        return Vec2D(self.x + other.x, self.y + other.y)

    def __sub__(self, other):
        return Vec2D(self.x - other.x, self.y - other.y)

    def __mul__(self, scalar):
        return Vec2D(self.x * scalar, self.y * scalar)

    def __truediv__(self, scalar):
        return Vec2D(self.x / scalar, self.y / scalar)

    # Construct a vector of length 1 with the same direction as this vector
    def unit(self):
        if self == Vec2D.ZERO:
            return Vec2D.ZERO
        return self / self.length()

    # Construct a vector that is normal to this vector
    def normal_vector(self):
        return Vec2D(-self.y, self.x)

    # Dot product of two vectors
    def dot(self, vec):
        return self.x * vec.x + self.y * vec.y

    # Vector length
    def length(self):
        return math.sqrt(self.x ** 2 + self.y ** 2)

    # Remember floating point equality rules if used
    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    # Remember floating point equality rules if used
    def __hash__(self):
        return hash((self.x, self.y))

    def __str__(self):
        return f"({self.x},{self.y})"


Vec2D.ZERO = Vec2D(0.0, 0.0)
Vec2D.LEFT = Vec2D(-1.0, 0.0)
Vec2D.RIGHT = Vec2D(1.0, 0.0)
Vec2D.UP = Vec2D(0.0, 1.0)
Vec2D.DOWN = Vec2D(0.0, -1.0)


#
class LineSegment2D:
    def __init__(self, start, end):
        self.start = start
        self.end = end

    # Swap start and end point
    def swap(self):
        return LineSegment2D(self.end, self.start)

    # The vector from start to end
    def vector(self):
        return self.end - self.start

    # Length of the segment
    def length(self):
        return (self.end - self.start).length()

    # Direction of the segment
    def direction(self):
        return (self.end - self.start).unit()

    # Compute the closest point on this segment to the given point (pt)
    def closest_point(self, pt):
        r = self.vector()

        t = ((pt.x * r.x - self.start.x * r.x) + (pt.y * r.y - self.start.y * r.y)) / (r.y * r.x + r.x * r.x)
        pt_on_segm = self.start + r * t

        if self.in_segment(pt_on_segm):
            return pt_on_segm
        else:
            a = (self.start - pt_on_segm).length()
            b = (self.end - pt_on_segm).length()
            return self.start if a < b else self.end

    # Is this point on the line segment
    def in_segment(self, pt):
        v = self.vector()
        t = pt - self.start
        if abs(t.y * v.x - t.x * v.y) < EPSILON:
            a = (t.x == 0.0 or (0.0 - EPSILON) <= (t.x / v.x) <= (1.0 + EPSILON))
            b = (t.y == 0.0 or (0.0 - EPSILON) <= (t.y / v.y) <= (1.0 + EPSILON))
            return a and b
        return False

    # The intersection point if the given line segment intersects this line segment, None if not
    def intersects(self, line):
        r = self.vector()
        s = line.vector()
        d = r.x * s.y - r.y * s.x

        if d == 0.0:
            if self.in_segment(line.start):
                return line.start
            elif self.in_segment(line.end):
                return line.end
            elif line.in_segment(self.start):
                return self.start
            elif line.in_segment(self.end):
                return self.end
            else:
                return None

        u = ((line.start.x - self.start.x) * r.y - (line.start.y - self.start.y) * r.x) / d
        t = ((line.start.x - self.start.x) * s.y - (line.start.y - self.start.y) * s.x) / d

        if 0.0 - EPSILON <= u <= 1.0 + EPSILON and 0.0 - EPSILON <= t <= 1.0 + EPSILON:
            return self.start + r * t
        else:
            return None

    def __str__(self):
        return f"LineSegment2D(start={self.start}, end={self.end})"

    # Remember floating point equality rules if used
    def __eq__(self, other):
        return self.start == other.start and self.end == other.end

    # Remember floating point equality rules if used
    def __hash__(self):
        return hash((self.start, self.end))
