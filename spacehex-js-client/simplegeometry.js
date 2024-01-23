// Used for FP number comparisons
// So we can control accuracy outside of pure FP accuracy.
const EPSILON = 0.0000001;

export class Vec2D {
    static ZERO = null;
    static LEFT = null;
    static RIGHT = null;
    static UP = null;
    static DOWN = null;

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    neg() {
        return new Vec2D(-this.x, -this.y);
    }

    add(other) {
        return new Vec2D(this.x + other.x, this.y + other.y);
    }

    sub(other) {
        return new Vec2D(this.x - other.x, this.y - other.y);
    }

    mul(scalar) {
        return new Vec2D(this.x * scalar, this.y * scalar);
    }

    div(scalar) {
        return new Vec2D(this.x / scalar, this.y / scalar);
    }

    // Construct a vector of length 1 with the same direction as this vector
    unit() {
        if (this.equals(Vec2D.ZERO)) {
            return Vec2D.ZERO;
        }
        return this.div(this.length());
    }

    // Construct a vector that is normal to this vector
    normalVector() {
        return new Vec2D(-this.y, this.x);
    }

    // Dot product of two vectors
    dot(vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    // Vector length
    length() {
        return Math.sqrt(this.x ** 2 + this.y ** 2);
    }

    // Remember floating point equality rules if used
    equals(other) {
        return this.x === other.x && this.y === other.y;
    }

    // Remember floating point equality rules if used
    hashCode() {
        return hash((this.x, this.y));
    }

    toString() {
        return `(${this.x},${this.y})`;
    }
}

Vec2D.ZERO = new Vec2D(0.0, 0.0);
Vec2D.LEFT = new Vec2D(-1.0, 0.0);
Vec2D.RIGHT = new Vec2D(1.0, 0.0);
Vec2D.UP = new Vec2D(0.0, 1.0);
Vec2D.DOWN = new Vec2D(0.0, -1.0);

export class LineSegment2D {
    constructor(start, end) {
        this.start = start;
        this.end = end;
    }

    // Swap start and end point
    swap() {
        return new LineSegment2D(this.end, this.start);
    }

    // The vector from start to end
    vector() {
        return this.end.sub(this.start);
    }

    // Length of the segment
    length() {
        return this.vector().length();
    }

    // Direction of the segment
    direction() {
        return this.vector().unit();
    }

    // Compute the closest point on this segment to the given point (pt)
    closestPoint(pt) {
        const r = this.vector();

        const t = ((pt.x * r.x - this.start.x * r.x) + (pt.y * r.y - this.start.y * r.y)) / (r.y * r.x + r.x * r.x);
        const ptOnSegm = this.start.add(r.mul(t));

        if (this.inSegment(ptOnSegm)) {
            return ptOnSegm;
        } else {
            const a = this.start.sub(ptOnSegm).length();
            const b = this.end.sub(ptOnSegm).length();
            return a < b ? this.start : this.end;
        }
    }

    // Is this point on the line segment
    inSegment(pt) {
        const v = this.vector();
        const t = pt.sub(this.start);
        if (Math.abs(t.y * v.x - t.x * v.y) < EPSILON) {
            const a = (t.x === 0.0 || ((t.x / v.x) >= (0.0 - EPSILON) && (t.x / v.x) <= (1.0 + EPSILON)));
            const b = (t.y === 0.0 || ((t.y / v.y) >= (0.0 - EPSILON) && (t.y / v.y) <= (1.0 + EPSILON)));
            return a && b;
        }
        return false;
    }

    // The intersection point if the given line segment intersects this line segment, null if not
    intersects(line) {
        const r = this.vector();
        const s = line.vector();
        const d = r.x * s.y - r.y * s.x;

        if (d === 0.0) {
            if (this.inSegment(line.start)) {
                return line.start;
            } else if (this.inSegment(line.end)) {
                return line.end;
            } else if (line.inSegment(this.start)) {
                return this.start;
            } else if (line.inSegment(this.end)) {
                return this.end;
            } else {
                return null;
            }
        }

        const u = ((line.start.x - this.start.x) * r.y - (line.start.y - this.start.y) * r.x) / d;
        const t = ((line.start.x - this.start.x) * s.y - (line.start.y - this.start.y) * s.x) / d;

        if ( (0.0 - EPSILON) <= u && u <= (1.0 + EPSILON) && (0.0 - EPSILON) <= t && t <= (1.0 + EPSILON)) {
            return this.start.add(r.mul(t));
        } else {
            return null;
        }
    }

    toString() {
        return `LineSegment2D(start=${this.start}, end=${this.end})`;
    }

    // Remember floating point equality rules if used
    equals(other) {
        return this.start.equals(other.start) && this.end.equals(other.end);
    }

    // Remember floating point equality rules if used
    hashCode() {
        return hash((this.start, this.end));
    }
}
