DEVELOPMENT
-----------
This contains details for how to create a client from scratch.
This is not needed to play, README.md for that.

## Client Messages
Messages and when they are passed

### Join message
Attaches the player name to the Websocket session and game.
```gameId``` is not needed for test games,.
Should be sent on Websocket session connect.
```
{
  "type": "join",
  "name": "Team s0nat",
  "gameId": "cwyzc"
}
```

### State message
Current state of the lander. Received every 100ms when game is running.
Acceleration is the acceleration applied last frame.
```
{
  "lander": {
    "position": {
      "x": 69.95,
      "y": 273.5499999999998
    },
    "velocity": {
      "x": -0.5,
      "y": -9.5
    },
    "acceleration": {
      "x": -5.0,
      "y": 5.0
    },
    "status": "FLYING",
    "finishTime": null
  },
  "type": "state"
}
```


### Input message
Input for a lander.
Can be sent any time, but if sent several times withing the 100ms window it will override
previous values.
```
{
  "gameId": "cwyzc",
  "acceleration": {
    "up": false,
    "left": false,
    "right": false
  },
  "type": "input"
}
```


### Environment message
This message is sent at the start of every game and stays constants throughout the game.
It is meant to be stored and referenced.

Contains:
* 2D line segments that must not be crossed. Crossing these means crashing.
* The goal that must be reached in space, must be withing 5 game units from the position, and at less then 2.5 game units/s velocity.
* Constants in the game (gravity, lander acceleration, update delta)
```
{
  "segments": [
    {
      "start": {
        "x": -1024.0,
        "y": -120.0
      },
      "end": {
        "x": -512.0,
        "y": -135.0
      }
    },
    {
      "start": {
        "x": -512.0,
        "y": -135.0
      },
      "end": {
        "x": -256.0,
        "y": -120.0
      }
    },
    ...
  ],
  "goal": {
    "x": -35.0,
    "y": -335.0
  },
  "constants": {
    "timeDeltaSeconds": 0.1,
    "gravity": 10.0,
    "landerAccelerationLeft": 5.0,
    "landerAccelerationRight": 5.0,
    "landerAccelerationUp": 15.0
  },
  "type": "env"
}

```