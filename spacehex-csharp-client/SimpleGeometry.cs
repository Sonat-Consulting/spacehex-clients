using Newtonsoft.Json;
namespace spacehex_csharp_client;

//Container to represents vectors and points in 2D.
public class Vec2D
{
    [JsonProperty("x")]
    public double X { get; set; }
    [JsonProperty("y")]
    public double Y { get; set; }

    public static Vec2D ZERO => new Vec2D(0.0, 0.0);
    public static Vec2D LEFT => new Vec2D(-1.0, 0.0);
    public static Vec2D RIGHT => new Vec2D(1.0, 0.0);
    public static Vec2D UP => new Vec2D(0.0, 1.0);
    public static Vec2D DOWN => new Vec2D(0.0, -1.0);

    public Vec2D(double x, double y)
    {
        X = x;
        Y = y;
    }

    //Invert vector
    public static Vec2D operator -(Vec2D v) => new Vec2D(-v.X, -v.Y);

    //Vector addition
    public static Vec2D operator +(Vec2D v1, Vec2D v2) => new Vec2D(v1.X + v2.X, v1.Y + v2.Y);

    //Subtract vector from another vector
    public static Vec2D operator -(Vec2D v1, Vec2D v2) => new Vec2D(v1.X - v2.X, v1.Y - v2.Y);

    //Scale vector bby a factor f
    public static Vec2D operator *(Vec2D v, double f) => new Vec2D(v.X * f, v.Y * f);

    //Scale vector by 1/f
    public static Vec2D operator /(Vec2D v, double f) => new Vec2D(v.X / f, v.Y / f);

    //Scale vector to lenght 1 or ZERO if ZERO
    public Vec2D Unit() => this == ZERO ? ZERO : this / Length();

    //Construct a vector normal to this vector, ZERO if ZERO
    public Vec2D NormalVector() => new Vec2D(-Y, X);

    //Dot product
    public double Dot(Vec2D vec) => X * vec.X + Y * vec.Y;

    //Length of vector
    public double Length() => Math.Sqrt(X * X + Y * Y);

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(this, obj)) return true;
        if (obj is not Vec2D other) return false;

        return X.Equals(other.X) && Y.Equals(other.Y);
    }

    public override int GetHashCode()
    {
        int hashCode = X.GetHashCode();
        hashCode = (hashCode * 31) + Y.GetHashCode();
        return hashCode;
    }

    public override string ToString() => $"({X},{Y})";
}

//Represents a line segment from point start to end
public class LineSegment2D
{
    [JsonProperty("start")]
    public Vec2D Start { get; set; }

    [JsonProperty("end")]
    public Vec2D End { get; set; }

    public LineSegment2D(Vec2D start, Vec2D end)
    {
        Start = start;
        End = end;
    }

    public LineSegment2D Swap() => new LineSegment2D(End, Start);

    //The vector from start to end
    public Vec2D Vector() => End - Start;

    //The length of the segment
    public double Length() => (End - Start).Length();

    //The direction of start to end of length 1
    public Vec2D Direction() => (End - Start).Unit();

    //The closest point on the line segment to the given vector
    public Vec2D ClosestPoint(Vec2D pt)
    {
        Vec2D r = Vector();
        double t = ((pt.X * r.X - Start.X * r.X) + (pt.Y * r.Y - Start.Y * r.Y)) / (r.Y * r.Y + r.X * r.X);
        Vec2D ptOnSegm = Start + r * t;

        if (InSegment(ptOnSegm))
            return ptOnSegm;
        else
        {
            double a = (Start - ptOnSegm).Length();
            double b = (End - ptOnSegm).Length();
            return a < b ? Start : End;
        }
    }

    //Is this point part of the line segment
    public bool InSegment(Vec2D pt)
    {
        Vec2D v = Vector();
        Vec2D t = pt - Start;

        if (Math.Abs(t.Y * v.X - t.X * v.Y) < EPSILON)
        {
            bool a = t.X == 0.0 || (t.X / v.X >= 0.0 - EPSILON && t.X / v.X <= 1.0 + EPSILON);
            bool b = t.Y == 0.0 || (t.Y / v.Y >= 0.0 - EPSILON && t.Y / v.Y <= 1.0 + EPSILON);

            return a && b;
        }

        return false;
    }

    //If the given line segment intersects this line segment return the intersection point, if not null
    public Vec2D? Intersects(LineSegment2D line)
    {
        Vec2D r = Vector();
        Vec2D s = line.Vector();
        double d = r.X * s.Y - r.Y * s.X;

        if (d == 0.0)
        {
            if (InSegment(line.Start)) return line.Start;
            if (InSegment(line.End)) return line.End;
            if (line.InSegment(Start)) return Start;
            if (line.InSegment(End)) return End;

            return null;
        }

        double u = ((line.Start.X - Start.X) * r.Y - (line.Start.Y - Start.Y) * r.X) / d;
        double t = ((line.Start.X - Start.X) * s.Y - (line.Start.Y - Start.Y) * s.X) / d;

        return (u >= 0.0 - EPSILON && u <= 1.0 + EPSILON && t >= 0.0 - EPSILON && t <= 1.0 + EPSILON)
            ? Start + r * t
            : (Vec2D?)null;
    }

    public override string ToString() => $"LineSegment2D(start={Start}, end={End})";

    public override bool Equals(object? obj)
    {
        if (ReferenceEquals(this, obj)) return true;
        if (obj is not LineSegment2D other) return false;

        return Start.Equals(other.Start) && End.Equals(other.End);
    }

    public override int GetHashCode()
    {
        int hashCode = Start.GetHashCode();
        hashCode = (hashCode * 31) + End.GetHashCode();
        return hashCode;
    }

    //Accuracy of geometric methods
    private const double EPSILON = 0.0000001;
}