package no.sonat.game

import no.sonat.game.geometry.LineSegment2D
import no.sonat.game.geometry.Vec2D
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Main")

val wsUri = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/test"
//val wsUri = "ws://spacehex.norwayeast.cloudapp.azure.com:7070/play"

var ag : AgentClient? = null

fun main() {
    logger.info("Start client")
    ag = AgentClient(
        wsUri = wsUri,
        room = "j1ycg", //Not in use for test runs
        name = "Team kOtlin",
        test = wsUri.endsWith("test"),
        strategy = ::calculateAcceleration,
        joinAction = {
            logger.info(it)
        }
    )
}

fun calculateAcceleration(env: Environment, lander : Lander) : Acceleration {
    return if(lander.position.y < 200) {
        sendDebug(
            listOf(
                LineSegment2D(
                    lander.position,
                    env.goal
                )
            )
        )
        Acceleration(up = true, left = false, right = false)
    } else {
        Acceleration(up = false, left = false, right = false)
    }
}

