Spacehex-clients
--------

### Getting started
* Clone this repository.
* Pick a language from the list below.
* Follow the instructions in the readme in that directory to start a test flight.

#### Specific instructions for each client listed here:
* [Kotlin](spacehex-kotlin-client)
* [Python](spacehex-python-client)
* [C#](spacehex-csharp-client)
* [JavaScript](spacehex-js-client)
* [Go](spacehex-go-client)

### General instructions
The game uses a 2D playing field of size 1024 x 768 Game units.
An example:

![Image](env-image.png)

### Gravity and mass
Gravity is a force from top to bottom. Your craft conveniently has mass 1.

### Goal
To reach the goal point (the red point), you need to be within 5.0 game units, 
at a speed below 2.5 GU/s.

### Segment collision 
The line segments shown in green should not be crossed. If two subsequent positions
of your craft passes through a line segment, your craft has crashed. 

The green lines that fade into the background are only for visual effect.

![Image](collision.png)

### Max flight time
5 minutes

### Out of bounds
If you go out of the visible area nothing will happen, but there is no point.
This restricts your search space, so use it wisely.

### Where to code (example in JS)

```javascript
function strategy(env, lander) {
    return {
        up: true,
        left: false,
        right: false
    }
}
```
This function should be implemented to control the craft. Each game 
tick is 100 ms, and this function will be called at that rate.
So to ensure you get time to receive, calculate and send the input 
before the next tick, calculation should ideally be below 25 ms.

If you are too late, the craft will keep using the last input it received.

Use globals to keep track of previous states.

The parameter ```env``` contains the environment. This data is static
and the js object is structured like this:
```json
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
    }
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
  }
}
```

The parameter ```lander``` contains the craft state, structured like this:
```json
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
    }
  }
}
```

This example fires the engine when the craft y position is below 200.
```javascript
function strategy(env, lander) {
    if (lander.position.y < 200) {
        return {
            up: true,
            left: false,
            right: false
        }
    } else {
        return {
            up: false,
            left: false,
            right: false
        }
    }
}
```
