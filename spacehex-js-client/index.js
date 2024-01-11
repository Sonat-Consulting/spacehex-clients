import WebSocket from 'ws';

const url = 'ws://localhost:7070/test';
const name = 'yolo'; // Team name
const room = 'asd123'; // Ignored for test runs, needed for competition

let currentEnv = {};

function strategy(env, lander) {
  if (lander.position.y < 200) {
    return {
      'gameId': room,
      'acceleration': {
          'up': true,
          'left': false,
          'right': false
      },
      'type': 'input'
    };
  } else {
    return {
      'gameId': room,
      'acceleration': {
          'up': false,
          'left': false,
          'right': false
      },
      'type': 'input'
    };
  }
}

// ------ INFRA AND MESSAGING CODE BELOW HERE, NO NEED TO CHANGE ------

let interval;
const ws = new WebSocket(url);

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
  const message = JSON.parse(data);
  const type = message.type;
  if (type === 'env') {
    currentEnv = message;
  } else if (type === 'state') {
    const action = strategy(currentEnv, message.lander);
    const inputMessage = JSON.stringify(action);
    console.log(inputMessage);
    ws.send(inputMessage);
  } else if (type === 'join') {
    console.log(message);
  } else {
    console.log('Got unexpected type: ' + type);
  }
});