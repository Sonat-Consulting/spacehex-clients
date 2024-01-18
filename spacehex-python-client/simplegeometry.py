import math

EPSILON = 0.0000001

class LineSegment2D:
    def __init__(self, start, end):
        self.start = start
        self.end = end

    def vector(self):
        return self.end.minus(self.start)

    def length(self):
        return self.vector().length()

    def direction(self):
        return self.vector().normalize()

    def intersects(self, line):
        b = self.start
        a = line.start
        w = self.direction()
        v = line.direction()

        num = (a.y * w.x - a.x * w.y - b.y * w.x + b.x * w.y)
        denom = (v.x * w.y - v.y * w.x)

        if denom == 0.0:
            return None

        t = num / denom
        pos = line.start.plus(v.times(t))

        l1maxX = max(line.start.x, line.end.x) + EPSILON
        l1minX = min(line.start.x, line.end.x) - EPSILON
        l1maxY = max(line.start.y, line.end.y) + EPSILON
        l1minY = min(line.start.y, line.end.y) - EPSILON

        l2maxX = max(self.start.x, self.end.x) + EPSILON
        l2minX = min(self.start.x, self.end.x) - EPSILON
        l2maxY = max(self.start.y, self.end.y) + EPSILON
        l2minY = min(self.start.y, self.end.y) - EPSILON

        if (
            l1minX < pos.x < l1maxX
            and l1minY < pos.y < l1maxY
            and l2minX < pos.x < l2maxX
            and l2minY < pos.y < l2maxY
        ):
            return pos
        return None

    def __str__(self):
        return str(self.start) + " -> " + str(self.end)


class Vec2D:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    @staticmethod
    def ZERO():
        return Vec2D(0.0, 0.0)

    @staticmethod
    def LEFT():
        return Vec2D(-1.0, 0.0)

    @staticmethod
    def RIGHT():
        return Vec2D(1.0, 0.0)

    @staticmethod
    def UP():
        return Vec2D(0.0, 1.0)

    @staticmethod
    def DOWN():
        return Vec2D(0.0, -1.0)

    def unaryPlus(self):
        return Vec2D(self.x, self.y)

    def unaryMinus(self):
        return Vec2D(-self.x, -self.y)

    def plus(self, v):
        return Vec2D(self.x + v.x, self.y + v.y)

    def minus(self, v):
        return Vec2D(self.x - v.x, self.y - v.y)

    def times(self, f):
        return Vec2D(self.x * f, self.y * f)

    def div(self, f):
        return Vec2D(self.x / f, self.y / f)

    def normalize(self):
        if self.length() == 0.0:
            return Vec2D.ZERO()
        return self.div(self.length())

    def dot(self, v):
        return self.x * v.x + self.y * v.y

    def angleTo(self, v):
        lgt = self.length()
        oLgt = v.length()
        if lgt == 0.0 or oLgt == 0.0:
            return 0.0
        return math.acos(self.dot(v) / (lgt * oLgt))

    def length(self):
        return math.sqrt(self.x * self.x + self.y * self.y)

    def projectOnto(self, target):
        dotTrg = target.dot(target)
        if dotTrg == 0.0:
            return Vec2D.ZERO()
        dot = self.dot(target)
        return target.times(dot / dotTrg)

    def __str__(self):
        return "("+str(self.x)+","+str(self.y)+")"
