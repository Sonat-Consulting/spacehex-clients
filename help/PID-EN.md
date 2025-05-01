PID for SpaceHex
----------------------
PID controllers ([PID regulator](https://snl.no/PID-regulator) in Norwegian) 
are the workhorse when it comes to moving from one state to another (e.g., from 
one position to another). A PID controller is not really appropriate for the 
entire problem, but it can be used to move the craft from position to position.

PID stands for Proportional (P), Integral (I), and Derivative (D). These are 
inputs to the controller.

### PID for the task
For our task, we can ignore I (Integral) and start with a PD controller.

* __Proportional:__ Measure of how far we are from the goal.
* __Derivative:__ The derivative, which here will be a measure of the change in how far we are from the goal over time. (Several types of change measurements can be used, the latest change in proportional is one possibility)

Each of these will be weighted, and the weighting determines how the controller behaves.

* __proportionalWeight:__ The higher, the more the error right now is prioritized. Overshooting can happen.
* __derivativeWeight:__ The higher, the more the algorithm takes into account how the error rate will be in the future. This dampens oscillations.

### Calculating proportional (P)
Proportional measures how far we are from the goal, a measure in the x-axis would then be:
```proportional = goal.x - posisiton.x```

### Calculating derivative (D)
A possible measure is the change in proportional from the previous tick.
```derivative = (proportional - previousProportional)/timeDelta```

### Translating from controller output to action
The weights here are constants tailored to the problem.

``` 
proportionalWeight = 1.0 derivativeWeight = 3.0 
output = proportionalproportionalWeight + derivativederivativeWeight
```
The controller will output a value. In our case, we then have to create an
acceleration action that tries to achieve this goal based on the output.

#### Determining weights:
Do it experimentally. What is good depends on other choices. 
Like how close to the landscape the chosen path is, and 
what maximum speed is acceptable, etc.

#### What a PID controller doesn't help us with
A PID controller moves a vessel from one position to another.
It doesn’t say when it has achieved this. It doesn’t consider whether
it has collided with anything along the way. Doing things without 
overshooting can be slow.
