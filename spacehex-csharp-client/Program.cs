using Newtonsoft.Json;
using Websocket.Client;
using ThreadingTimer = System.Threading.Timer;

namespace spacehex_csharp_client;

class Program
{
    static readonly string NAME = "Team C#"; // Team name
    static readonly string ROOM = "692pa"; // Ignored for test runs, needed for competition

    static Environment? ENV;

    static Acceleration Strategy(Environment? env, Lander lander)
    {
        if (lander.Position.Y < 200)
        {
            return new()
            {
                Up = true,
                Left = false,
                Right = false,
            };

        }
        else
        {
            return new()
            {
                Up = false,
                Left = false,
                Right = false,
            };
        }
    }

    static void Main(string[] args)
    {
        var url = new Uri("ws://spacehex.norwayeast.cloudapp.azure.com:7070/test");
        // var url = new Uri("ws://spacehex.norwayeast.cloudapp.azure.com:7070/play");

        // ------ INFRA AND MESSAGING CODE BELOW HERE, NO NEED TO CHANGE------

        var exitEvent = new ManualResetEvent(false);

        using var client = new WebsocketClient(url);
        client.ReconnectionHappened.Subscribe(info =>
            {
                if (info.Type == ReconnectionType.Initial)
                {
                    Console.WriteLine("Opened connection");
                    Join joinMessage = new()
                    {
                        Name = NAME,
                        GameId = ROOM,
                    };
                    string json = JsonConvert.SerializeObject(joinMessage);
                    client.Send(json);
                }
                else
                {
                    Console.WriteLine($"Reconnection happened, please restart the program");
                }
            }
        );

        client.MessageReceived.Subscribe(data =>
        {
            if(data.ToString() == "PONG")
            {
                Console.WriteLine("Got pong");
            } else
            {
            dynamic message = JsonConvert.DeserializeObject(data.ToString())!;
                if (message.type == "env")
                {
                    ENV = JsonConvert.DeserializeObject<Environment>(data.ToString())!;
                }
                else if (message.type == "state")
                {
                    State state = JsonConvert.DeserializeObject<State>(data.ToString())!;
                    Action action = new() { GameId = ROOM, Acceleration = Strategy(ENV, state.Lander), Type = "input" };
                    string inputMessage = JsonConvert.SerializeObject(action);
                    Console.WriteLine(inputMessage);
                    client.Send(inputMessage);
                }
                else if (message.type == "join")
                {
                    Console.WriteLine(data);
                }
                else
                {
                    Console.WriteLine("Got unexpected type: " + message.type);
                }
            }

        });

        var timer = new ThreadingTimer(_ => {
            Console.WriteLine("Ping");
            client.Send("PING");
        }, null, TimeSpan.FromSeconds(10), TimeSpan.FromSeconds(10));

        client.Start();
        exitEvent.WaitOne();
    }
}
