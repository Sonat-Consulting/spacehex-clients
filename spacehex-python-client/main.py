import websocket
import _thread
import time
import rel
import json

url = "ws://localhost:7070/test"
name = "Team pyth0n"  # Team name
room = "6dnxu"  # Ignored for test runs, needed for competition

current_env = {}


def strategy(env, lander):
    if lander["position"]["y"] < 200.0:
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
    message = json.loads(message)
    type = message["type"]
    if type == "env":
        global current_env
        current_env = message
    elif type == "state":
        action = strategy(current_env, message["lander"])
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


if __name__ == "__main__":
    websocket.enableTrace(False)
    ws = websocket.WebSocketApp(url,
                                on_open=on_open,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)

    ws.run_forever(dispatcher=rel, ping_interval=15, ping_payload="PING")

    rel.signal(2, rel.abort)  # Keyboard Interrupt
    rel.dispatch()
