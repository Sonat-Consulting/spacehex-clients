using Newtonsoft.Json;
using Websocket.Client;

namespace spacehex_csharp_client;

class Program
{
    static readonly string NAME = "yolo"; // Team name
    static readonly string ROOM = "asd123"; // Ignored for test runs, needed for competition

    static Environment? ENV;

    static Action Strategy(Environment? env, State state)
    {
        if (state.Lander.Position.Y < 200)
        {
            return new()
            {
                GameId = ROOM,
                Acceleration = new()
                {
                    Up = true,
                    Left = false,
                    Right = false,
                }
            };
        }
        else
        {
            return new()
            {
                GameId = ROOM,
                Acceleration = new()
                {
                    Up = false,
                    Left = false,
                    Right = false,
                }
            };
        }
    }

    // ------ INFRA AND MESSAGING CODE BELOW HERE, NO NEED TO CHANGE ------

    static void Main(string[] args)
    {
        var exitEvent = new ManualResetEvent(false);

        var url = new Uri("ws://localhost:7070/test");

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
            dynamic message = JsonConvert.DeserializeObject(data.ToString())!;
            if (message.type == "env")
            {
                ENV = JsonConvert.DeserializeObject<Environment>(data.ToString())!;
            }
            else if (message.type == "state")
            {
                State state = JsonConvert.DeserializeObject<State>(data.ToString())!;
                Action action = Strategy(ENV, state);
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

        });

        client.Start();

        exitEvent.WaitOne();
    }
}
