package main

import (
	"log"
	"os"
)

var logger = log.New(os.Stdout, "Main", log.LstdFlags)

// Some changes will have to be done to these when it comes to the actual
// game as this is configured with test wsURI.
func main() {
	logger.Println("Start client")
	agentClient := AgentClient{
		WsURI:      "ws://51.120.245.215:7070/test",
		//WsURI:      "ws://51.120.245.215:7070/play",
		Room:       "692pa",
		Name:       "Team Go",
		Strategy:   calculateFlight,
		JoinAction: func(msg string) { logger.Println(msg) },
	}
	agentClient.Run()
}

// Your main focus should be below this comment.
// You can add more functions, globals etc to this file if you want.
// Good luck!

func calculateFlight(env Environment, lander Lander) Acceleration {
	if lander.Position.Y < float64(200) {
		return Acceleration{Up: true, Left: false, Right: false}
	} else {
		return Acceleration{Up: false, Left: false, Right: false}
	}
}
