package main

import (
	"encoding/json"
	"fmt"
	"log"
	"sync"
	"time"

	"github.com/TwiN/go-color"
	"github.com/gorilla/websocket"
)

type LanderStatus string

const (
	Flying       LanderStatus = "FLYING"
	Completed    LanderStatus = "COMPLETED"
	Crashed      LanderStatus = "CRASHED"
	DidNotFinish LanderStatus = "DID_NOT_FINISH"
)

type Vec2D struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
}

type Lander struct {
	Position   Vec2D        `json:"position"`
	Velocity   Vec2D        `json:"velocity"`
	Status     LanderStatus `json:"status"`
	FinishTime *float64     `json:"finishTime,omitempty"`
}

type Acceleration struct {
	Up    bool `json:"up"`
	Left  bool `json:"left"`
	Right bool `json:"right"`
}

type Input struct {
	GameID       string       `json:"gameId"`
	Acceleration Acceleration `json:"acceleration"`
	Type         string       `json:"type"`
}

type Constants struct {
	TimeDeltaSeconds        float64 `json:"timeDeltaSeconds"`
	Gravity                 float64 `json:"gravity"`
	LanderAccelerationLeft  float64 `json:"landerAccelerationLeft"`
	LanderAccelerationRight float64 `json:"landerAccelerationRight"`
	LanderAccelerationUp    float64 `json:"landerAccelerationUp"`
}

type State struct {
	Lander Lander `json:"lander"`
	Type   string `json:"type"`
}

type Environment struct {
	Segments  []LineSegment2D `json:"segments"`
	Goal      Vec2D           `json:"goal"`
	Constants Constants       `json:"constants"`
	Type      string          `json:"type"`
}

type LineSegment2D struct {
	Start Vec2D `json:"start"`
	End   Vec2D `json:"end"`
}

type AgentClient struct {
	WsURI      string
	Room       string
	Name       string
	Strategy   func(Environment, Lander) Acceleration
	JoinAction func(string)
	mu         sync.Mutex
}

var environment Environment

func (a *AgentClient) Run() {
	c, _, err := websocket.DefaultDialer.Dial(a.WsURI, nil)
	if err != nil {
		log.Fatal("dial:", err)
	}
	defer c.Close()

	go func() {
		for {
			select {
			case <-time.After(15 * time.Second):
				err := c.WriteMessage(websocket.PingMessage, nil)
				if err != nil {
					log.Println("Ping error:", err)
					return
				}
			}
		}
	}()

	err = c.WriteJSON(map[string]interface{}{
		"type":   "join",
		"name":   a.Name,
		"gameId": a.Room,
	})
	if err != nil {
		log.Fatal("join:", err)
	}

	go a.readMessages(c)
	a.sendInputLoop(c)
}

func (a *AgentClient) readMessages(c *websocket.Conn) {
	for {
		_, message, err := c.ReadMessage()
		if err != nil {
			log.Println("read:", err)
			return
		}
		a.handleMessage(c, message)
	}
}

func (a *AgentClient) handleMessage(c *websocket.Conn, message []byte) {
	if "PONG" == string(message) {
		return
	}
	var msg map[string]json.RawMessage
	err := json.Unmarshal(message, &msg)
	if err != nil {
		log.Println("Unmarshal error:", err)
		return
	}

	var msgType string
	err = json.Unmarshal(msg["type"], &msgType)
	if err != nil {
		log.Println("Type Unmarshal error:", err)
		return
	}

	switch msgType {
	case "env":
		err := json.Unmarshal(message, &environment)
		if err != nil {
			log.Println("Env Unmarshal error:", err)
			return
		}
		// Handle environment update
		fmt.Println("Received Environment:", environment)
	case "state":
		var state State
		err := json.Unmarshal(message, &state)
		if err != nil {
			log.Println("State Unmarshal error:", err)
			return
		}

		// Handle state update and send input
		acceleration := a.Strategy(environment, state.Lander)
		fmt.Println("State: ", state, "Acceleration: ", func() string {
			if acceleration.Up {
				return color.InGreen("Up")
			} else if acceleration.Left {
				return color.InGreen("Left")
			} else if acceleration.Right {
				return color.InGreen("Right")
			}
			return "None"
		}())
		a.sendInput(c, acceleration)
	case "join":
		var joinResponse map[string]string
		err := json.Unmarshal(message, &joinResponse)
		if err != nil {
			log.Println("Join Unmarshal error:", err)
			return
		}
		fmt.Println("Joined room. Visit URL:", joinResponse["url"])
	case "error":
		var errorResponse map[string]interface{}
		err := json.Unmarshal(message, &errorResponse)
		if err != nil {
			log.Println("Error Unmarshal error:", err)
			return
		}
		log.Println("Game error:", errorResponse)
		// Consider handling error and disconnect if needed
	default:
		log.Println("Got state with type", msgType)
	}
}

func (a *AgentClient) sendInputLoop(c *websocket.Conn) {
	for {
		time.Sleep(1 * time.Second)
	}
}

func (a *AgentClient) sendInput(c *websocket.Conn, acceleration Acceleration) {
	input := Input{
		GameID:       a.Room,
		Acceleration: acceleration,
		Type:         "input",
	}
	err := c.WriteJSON(input)
	if err != nil {
		log.Println("write:", err)
		return
	}
}
