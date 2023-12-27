package no.sonat.game

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.neovisionaries.ws.client.*
import org.slf4j.LoggerFactory

enum class LanderStatus { FLYING, COMPLETED, CRASHED }

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

class AgentClient(
    val wsUri : String,
    val room : String,
    val name : String,
    val strategy : (state : Lander) -> Acceleration,
    val joinAction : (String) -> Unit = {}) {

    private val logger = LoggerFactory.getLogger("Agent")
    val objectMapper = ObjectMapper().registerKotlinModule()

    val agent = WebSocketFactory()
        .createSocket(wsUri)
        .setPingInterval(60*1000)
        .setPingPayloadGenerator { "PING".toByteArray() }
        .addListener(object : WebSocketAdapter() {

            override fun onConnected(websocket: WebSocket, headers: MutableMap<String, MutableList<String>>) {
                val msg = objectMapper.writeValueAsString(mapOf("type" to "join", "name" to name) )
                logger.info(msg)
                websocket.sendText(msg)
            }

            override fun onTextMessage(websocket: WebSocket, text: String) {
                logger.info("Received: $text")
                val response = objectMapper.readTree(text)
                when(response.get("type").asText()) {
                    "state" -> {
                        val lander = objectMapper.readValue(text,Lander::class.java)
                        val acceleration = strategy(lander)
                        websocket.sendText(objectMapper.writeValueAsString(acceleration))
                    }
                    "join" -> {
                        logger.info("start by visiting url {}",text)
                        joinAction(text)
                    }
                    "end" -> {
                        logger.info("Ended")
                        websocket.disconnect()
                    }
                    "error" -> {
                        logger.info("Game error {}",response)
                        websocket.disconnect()
                    }
                }
            }

            override fun onError(websocket: WebSocket, cause: WebSocketException) {
                logger.error("Websocket failure",cause)
            }

            override fun onConnectError(websocket: WebSocket, exception: WebSocketException?) {
                logger.error("Websocket failure",exception)
            }

            override fun onPongFrame(websocket: WebSocket, frame: WebSocketFrame) {
                logger.info("Got pong ? ${frame.payload}")
            }
        })
        .connect()

}