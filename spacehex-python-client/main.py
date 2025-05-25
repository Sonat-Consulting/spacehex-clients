import websocket
import threading
import rel
import json

from simplegeometry import Vec2D, LineSegment2D

#url = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/play"
url = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/test"

name = "Team pyth0n"  # Team name
room = "y5q96"  # Ignored for test runs, needed for competition

current_env = {}


def calculate_acceleration(env, lander):
    if lander["position"]["y"] < 200.0:
        x = lander["position"]["x"]
        y = lander["position"]["y"]
        gX = env["goal"]["x"]
        gY = env["goal"]["y"]
        send_debug([{"start":{"x":x,"y":y},"end" :{"x":gX,"y":gY}}])
        return {
            "gameId": room,
            "acceleration": {
                "up": True,
                "left": False,
                "right": False
            },
            "type": "input"
        }
    else:
        return {
            "gameId": room,
            "acceleration": {
                "up": False,
                "left": False,
                "right": False
            },
            "type": "input"
        }


# ------ INFRA AND MESSAGING CODE BELOW HERE, NO NEED TO CHANGE ------

def on_message(ws, message):
    print(message)
    if message == "PONG":
        return
    message = json.loads(message)
    type = message["type"]
    if type == "env":
        global current_env
        current_env = message
    elif type == "state":
        action = calculate_acceleration(current_env, message["lander"])
        input_message = json.dumps(action)
        print(input_message)
        ws.send(input_message)
    elif type == "join":
        print(message)
    else:
        print("Got unexpected type: " + type)


def on_error(ws, error):
    print(error)


def on_close(ws, close_status_code, close_msg):
    print("### closed ###")


def on_open(ws):
    print("Opened connection")
    join = {
        "name": name,
        "gameId": room,
        "type": "join"
    }
    join_message = json.dumps(join)
    ws.send(join_message)


def send_debug(segments):
    if url.endswith("test"):
        debug = {
        "segments": segments,
        "type": "debug"
        }
        debug_message = json.dumps(debug)
        ws.send(debug_message)


if __name__ == "__main__":
    websocket.enableTrace(False)
    ws = websocket.WebSocketApp(url,
                                on_open=on_open,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)

    def do_ping():
        print("Sending ping")
        ws.send("PING")
        t = threading.Timer(15,do_ping)
        t.daemon = True
        t.start()

    ws.run_forever(dispatcher=rel, ping_interval=15, ping_payload="PING")

    do_ping()

    rel.signal(2, rel.abort)  # Keyboard Interrupt
    rel.dispatch()
