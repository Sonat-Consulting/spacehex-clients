import WebSocket from 'ws';
import { LineSegment2D, Vec2D } from './simplegeometry.js';

const url = 'ws://spacehex.norwayeast.cloudapp.azure.com:7070/test';
//const url = 'ws://spacehex.norwayeast.cloudapp.azure.com:7070/play'; 

const name = 'Team JS'; // Team name
const room = '692pa'; // Ignored for test runs, needed for competition

let currentEnv = {};

function strategy(env, lander) {
  if(lander.position.y < 200) {
    sendDebug(
        [new LineSegment2D(lander.position,env.goal)]
    )
    return {
      up: true,
      left: false,
      right: false
    }
  }
  else {
    return {
      up: false,
      left: false,
      right: false
    }
  }
}

// ------ INFRA AND MESSAGING CODE BELOW HERE, NO NEED TO CHANGE ------

let interval;
const ws = new WebSocket(url);

function sendDebug(segments) {
  if(url.endsWith("test")) {
    ws.send(JSON.stringify({
      type: "debug",
      segments: segments
    }))
  }
}

ws.on('error', console.error);

ws.on('close', function close() {
  clearInterval(interval);
  console.log('### closed ###');
})

ws.on('open', function open() {
  console.log('Opened connection');
  const join = {
    'name': name,
    'gameId': room,
    'type': 'join'
  }
  const joinMessage = JSON.stringify(join);
  ws.send(joinMessage);

  interval = setInterval(() => {
    ws.send('PING');
  }, 15000);
});

ws.on('message', function message(data) {
  try {
    if(data === "PONG") {
      console.log("Got PONG")
    } else {
      const message = JSON.parse(data);
      const type = message.type;
      if (type === 'env') {
        currentEnv = message;
      } else if (type === 'state') {
        const action = {gameId: room, acceleration: strategy(currentEnv, message.lander), type: 'input'};
        const inputMessage = JSON.stringify(action);
        console.log(inputMessage);
        ws.send(inputMessage);
      } else if (type === 'join') {
        console.log(message);
      } else {
        console.log('Got unexpected type: ' + type);
      }
    }
  } catch (e) {
    console.log("failed with error: "+ e + "for data" + data)
  }
});