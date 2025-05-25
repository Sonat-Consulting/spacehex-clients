package main

import (
	"log"
	"os"
	"strings"
)

var logger = log.New(os.Stdout, "Main", log.LstdFlags)

var Client AgentClient

var WsURI = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/test"

//var WsURI = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/play"

// Some changes will have to be done to these when it comes to the actual
// game as this is configured with test wsURI.
func main() {
	logger.Println("Start client")
	Client = AgentClient{
		WsURI:      WsURI,
		Room:       "692pa",
		Name:       "Team Go",
		Strategy:   calculateFlight,
		JoinAction: func(msg string) { logger.Println(msg) },
		Test:       strings.HasSuffix(WsURI, "test"),
	}
	Client.Run()
}

// Your main focus should be below this comment.
// You can add more functions, globals etc to this file if you want.
// Good luck!

func calculateFlight(env Environment, lander Lander) Acceleration {
	if lander.Position.Y < float64(200) {
		v := LineSegment2D{
			Start: lander.Position,
			End:   env.Goal,
		}
		Client.sendDebug([]LineSegment2D{v})
		return Acceleration{Up: true, Left: false, Right: false}
	} else {
		return Acceleration{Up: false, Left: false, Right: false}
	}
}
