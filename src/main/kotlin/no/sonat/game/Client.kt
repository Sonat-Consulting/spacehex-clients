package no.sonat.game

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.neovisionaries.ws.client.*
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

enum class LanderStatus { FLYING, COMPLETED, CRASHED, DID_NOT_FINISH }

data class Vec2D(
    val x:Double,
    val y:Double)


data class Lander(
    val position : Vec2D,
    val velocity : Vec2D,
    val status : LanderStatus,
    val finishTime : Double?
)

data class Acceleration(
    val up : Boolean,
    val left : Boolean,
    val right : Boolean
)

data class Input(
    val acceleration: Acceleration
) {
    val type = "input"
}

data class Constants(
    val timeDeltaSeconds : Double = 0.1,
    val gravity : Double =  10.0,
    val landerAccelerationLeft : Double = 5.0,
    val landerAccelerationRight : Double = 5.0,
    val landerAccelerationUp : Double = 15.0,
)

data class State(val lander: Lander) {
    val type: String = "state"
}

data class Environment(val segments: List<LineSegment2D>, val goal : Vec2D, val constants: Constants ) {
    val type: String = "env"
}

data class LineSegment2D(val start : Vec2D, val end : Vec2D)

class AgentClient(
    val wsUri : String,
    val room : String,
    val name : String,
    val strategy : (environment : Environment, state : Lander) -> Acceleration,
    val joinAction : (String) -> Unit = {}) {

    private val logger = LoggerFactory.getLogger("Agent")
    val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()

    val currentEnvironment = AtomicReference<Environment>(null)

    val agent = WebSocketFactory()
        .createSocket(wsUri)
        .setPingInterval(15*1000)
        .setPingPayloadGenerator { "PING".toByteArray() }
        .addListener(object : WebSocketAdapter() {

            override fun onConnected(websocket: WebSocket, headers: MutableMap<String, MutableList<String>>) {
                val msg = objectMapper.writeValueAsString(
                    mapOf(
                        "type" to "join",
                        "name" to name,
                        "gameId" to room
                    )
                )
                logger.info(msg)
                websocket.sendText(msg)
            }

            override fun onTextMessage(websocket: WebSocket, text: String) {
                try {
                    logger.info("Received: $text")
                    val response = objectMapper.readTree(text)
                    val type = response.get("type").asText()
                    when (type) {
                        "env" -> {
                            val environment = objectMapper.readValue(text, Environment::class.java)
                            currentEnvironment.set(environment)
                        }
                        "state" -> {
                            val state = objectMapper.readValue(text, State::class.java)
                            val acceleration = strategy(currentEnvironment.get(),state.lander)
                            logger.info("acceleration: $acceleration")
                            websocket.sendText(objectMapper.writeValueAsString(Input(acceleration = acceleration)))
                        }
                        "join" -> {
                            logger.info("start by visiting url {}", text)
                            joinAction(text)
                        }
                        "error" -> {
                            logger.info("Game error {}", response)
                            websocket.disconnect()
                        }
                        else -> {
                            logger.warn("Got state with type {}", type)
                        }
                    }
                } catch (e : Exception) {
                    logger.error("Unexpected",e)
                }
            }

            override fun onError(websocket: WebSocket, cause: WebSocketException) {
                logger.error("Websocket failure",cause)
            }

            override fun onConnectError(websocket: WebSocket, exception: WebSocketException?) {
                logger.error("Websocket failure",exception)
            }

            override fun onPongFrame(websocket: WebSocket, frame: WebSocketFrame) {
                logger.info("Got pong ${String(frame.payload)}")
            }
        })
        .connect()

}