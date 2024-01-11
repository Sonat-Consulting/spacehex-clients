using Newtonsoft.Json;

namespace spacehex_csharp_client;

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
  public required Segment[] Segments { get; set; }
  [JsonProperty("goal")]
  public required Point Goal { get; set; }
  [JsonProperty("constants")]
  public required Constants Constants { get; set; }
  [JsonProperty("type")]
  public string Type { get; set; } = "env";
}

class Segment
{
  [JsonProperty("start")]
  public required Point Start { get; set; }
  [JsonProperty("end")]
  public required Point End { get; set; }
  [JsonProperty("isHorizontal")]
  public required bool IsHorizontal { get; set; }
  [JsonProperty("direction")]
  public required Point Direction { get; set; }
  [JsonProperty("isVertical")]
  public required bool IsVertical { get; set; }
}

class Point
{
  [JsonProperty("x")]
  public required double X { get; set; }
  [JsonProperty("y")]
  public required double Y { get; set; }
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
  public required Point Position { get; set; }
  [JsonProperty("velocity")]
  public required Point Velocity { get; set; }
  [JsonProperty("acceleration")]
  public required Point Acceleration { get; set; }
  [JsonProperty("status")]
  public required string Status { get; set; }
  [JsonProperty("finishTime")]
  public required double? FinishTime { get; set; }
}