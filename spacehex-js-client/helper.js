const EPSILON = 0.0000001;
 
class LineSegment2D {
  constructor(start, end) {
    this.start = start;
    this.end = end;
  }

  vector() {
    return this.end.minus(this.start);
  }

  length() {
    return this.vector().length();
  }

  direction() {
    return this.vector().normalize();
  }

  intersects(line) {
    var b = this.start;
    var a = line.start;
    var w = this.direction();
    var v = line.direction();

    var num = (a.y*w.x - a.x*w.y - b.y*w.x + b.x*w.y);

    var denom = (v.x*w.y - v.y*w.x);

    if(denom === 0.0) {
      return null;
    }

    var t = num / denom;
    var pos = line.start.plus(v.times(t));

    var l1maxX = Math.max(line.start.x, line.end.x) + EPSILON;
    var l1minX = Math.min(line.start.x, line.end.x) - EPSILON;
    var l1maxY = Math.max(line.start.y, line.end.y) + EPSILON;
    var l1minY = Math.min(line.start.y, line.end.y) - EPSILON;

    var l2maxX = Math.max(this.start.x, this.end.x) + EPSILON;
    var l2minX = Math.min(this.start.x, this.end.x) - EPSILON;
    var l2maxY = Math.max(this.start.y, this.end.y) + EPSILON;
    var l2minY = Math.min(this.start.y, this.end.y) - EPSILON;

    if (
      pos.x > l1minX && pos.x < l1maxX &&
      pos.y > l1minY && pos.y < l1maxY &&
      pos.x > l2minX && pos.x < l2maxX &&
      pos.y > l2minY && pos.y < l2maxY
    ) {
      return pos;
    }
    return null;
  }
}

class Vec2D {
  constructor(x, y) {
    this.x = x;
    this.y = y;
  }

  static get ZERO() {
    return new Vec2D(0.0, 0.0);
  }

  static get LEFT() {
    return new Vec2D(-1.0, 0.0);
  }

  static get RIGHT() {
    return new Vec2D(1.0, 0.0);
  }

  static get UP() {
    return new Vec2D(0.0, 1.0);
  }

  static get DOWN() {
    return new Vec2D(0.0, -1.0);
  }

  unaryPlus() {
    return new Vec2D(this.x, this.y);
  }

  unaryMinus() {
    return new Vec2D(-this.x, -this.y);
  }

  plus(v) {
    return new Vec2D(this.x + v.x, this.y + v.y);
  }

  minus(v) {
    return new Vec2D(this.x - v.x, this.y - v.y);
  }

  times(f) {
    return new Vec2D(this.x * f, this.y * f);
  }

  div(f) {
    return new Vec2D(this.x / f, this.y / f);
  }

  normalize() {
    if (this.length() === 0.0) {
      return Vec2D.ZERO;
    }
    return this.div(this.length());
  }

  dot(v) {
    return this.x * v.x + this.y * v.y;
  }

  angleTo(v) {
    var lgt = this.length();
    var oLgt = v.length();
    if(lgt === 0.0 || oLgt === 0.0) {
      return 0.0;
    }
    return Math.acos(this.dot(v) / (lgt * oLgt));
  }

  length() {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  projectOnto(target) {
    var dotTrg = target.dot(target);
    if(lgt === 0.0) {
      return Vec2D.ZERO;
    }
    var dot = this.dot(target);
    return target.times(dot / dotTrg);
  }
}

export { LineSegment2D, Vec2D };