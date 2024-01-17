using Newtonsoft.Json;

namespace spacehex_csharp_client;

class LineSegment2D
{
  [JsonProperty("start")]
  public required Vec2D Start { get; set; }

  [JsonProperty("end")]
  public required Vec2D End { get; set; }
  [JsonProperty("isHorizontal")]
  public required bool IsHorizontal { get; set; }
  [JsonProperty("direction")]
  public required Vec2D Direction { get; set; }
  [JsonProperty("isVertical")]
  public required bool IsVertical { get; set; }

  public Vec2D Vector() => End - Start;

  public double Length() => Vector().Length();

  public Vec2D Normalized() => Vector().Normalized();

  public Vec2D? Intersects(LineSegment2D line)
  {
    var b = Start;
    var a = line.Start;
    var w = Direction;
    var v = line.Direction;

    var num = a.Y * w.X - a.X * w.Y - b.Y * w.X + b.X * w.Y;
    var denom = v.X * w.Y - v.Y * w.X;

    if (denom == 0.0) return null;

    var t = num / denom;
    var pos = line.Start + line.Direction * t;

    var EPSILON = 0.0000001;
    var l1maxX = Math.Max(line.Start.X, line.End.X) + EPSILON;
    var l1minX = Math.Min(line.Start.X, line.End.X) - EPSILON;
    var l1maxY = Math.Max(line.Start.Y, line.End.Y) + EPSILON;
    var l1minY = Math.Min(line.Start.Y, line.End.Y) - EPSILON;

    var l2maxX = Math.Max(Start.X, End.X) + EPSILON;
    var l2minX = Math.Min(Start.X, End.X) - EPSILON;
    var l2maxY = Math.Max(Start.Y, End.Y) + EPSILON;
    var l2minY = Math.Min(Start.Y, End.Y) - EPSILON;

    if (
      pos.X >= l1minX && pos.X <= l1maxX
      && pos.Y >= l1minY && pos.Y <= l1maxY
      && pos.X >= l2minX && pos.X <= l2maxX
      && pos.Y >= l2minY && pos.Y <= l2maxY) return pos;
    return null;
  }
}

class Vec2D
{
  [JsonProperty("x")]
  public required double X { get; set; }
  [JsonProperty("y")]
  public required double Y { get; set; }

  public static Vec2D ZERO => new() { X = 0.0, Y = 0.0 };
  public static Vec2D UP => new() { X = 0.0, Y = 1.0 };
  public static Vec2D DOWN => new() { X = 0.0, Y = -1.0 };
  public static Vec2D LEFT => new() { X = -1.0, Y = 0.0 };
  public static Vec2D RIGHT => new() { X = 1.0, Y = 0.0 };


  public static Vec2D operator +(Vec2D v) => new() { X = +v.X, Y = +v.Y };

  public static Vec2D operator -(Vec2D v) => new() { X = -v.X, Y = -v.Y };

  public static Vec2D operator +(Vec2D a, Vec2D b) => new() { X = a.X + b.X, Y = a.Y + b.Y };

  public static Vec2D operator -(Vec2D a, Vec2D b) => new() { X = a.X - b.X, Y = a.Y - b.Y };

  public static Vec2D operator *(Vec2D a, double b) => new() { X = a.X * b, Y = a.Y * b };

  public static Vec2D operator /(Vec2D a, double b) => new() { X = a.X / b, Y = a.Y / b };

  public double Length() => Math.Sqrt(X * X + Y * Y);
  public double Dot(Vec2D v) => X * v.X + Y * v.Y;
  public double AngleTo(Vec2D v)
  {
    var lgt = Length();
    var oLgt = v.Length();
    if (lgt == 0.0 || oLgt == 0.0) return 0.0;
    return Math.Acos(Dot(v) / (Length() * v.Length()));
  }
  public Vec2D ProjectOnto(Vec2D v)
  {
    var dotTrg = v.Dot(v);
    if (dotTrg == 0.0) return ZERO;
    var dot = Dot(v);
    return v * (dot / dotTrg);
  }

  public Vec2D Normalized()
  {
    var lgt = Length();
    if (lgt == 0.0) return ZERO;
    return this / lgt;
  }
}

// ------ Class mainly used to parse JSON ------

class Join
{
  [JsonProperty("name")]
  public required string Name { get; set; }
  [JsonProperty("gameId")]
  public required string GameId { get; set; }
  [JsonProperty("type")]
  public string Type { get; set; } = "join";
}

class Action
{
  [JsonProperty("gameId")]
  public required string GameId { get; set; }
  [JsonProperty("acceleration")]
  public required Acceleration Acceleration { get; set; }
  [JsonProperty("type")]
  public string Type { get; set; } = "input";
}

class Acceleration
{
  [JsonProperty("up")]
  public required bool Up { get; set; }
  [JsonProperty("left")]
  public required bool Left { get; set; }
  [JsonProperty("right")]
  public required bool Right { get; set; }

}


class Environment
{
  [JsonProperty("segments")]
  public required LineSegment2D[] Segments { get; set; }
  [JsonProperty("goal")]
  public required Vec2D Goal { get; set; }
  [JsonProperty("constants")]
  public required Constants Constants { get; set; }
  [JsonProperty("type")]
  public string Type { get; set; } = "env";
}
class Constants
{
  [JsonProperty("timeDeltaSeconds")]
  public required double TimeDeltaSeconds { get; set; }
  [JsonProperty("gravity")]
  public required double Gravity { get; set; }
  [JsonProperty("landerAccelerationLeft")]
  public required double LanderAccelerationLeft { get; set; }
  [JsonProperty("landerAccelerationRight")]
  public required double LanderAccelerationRight { get; set; }
  [JsonProperty("landerAccelerationUp")]
  public required double LanderAccelerationUp { get; set; }
}

class State
{
  [JsonProperty("lander")]
  public required Lander Lander { get; set; }
  [JsonProperty("type")]
  public string Type { get; set; } = "state";
}

class Lander
{
  [JsonProperty("position")]
  public required Vec2D Position { get; set; }
  [JsonProperty("velocity")]
  public required Vec2D Velocity { get; set; }
  [JsonProperty("acceleration")]
  public required Vec2D Acceleration { get; set; }
  [JsonProperty("status")]
  public required string Status { get; set; }
  [JsonProperty("finishTime")]
  public required double? FinishTime { get; set; }
}