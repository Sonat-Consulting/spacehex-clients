package no.sonat.game

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Start client")
    val ag = AgentClient(
        wsUri = "ws://4.235.120.113:7070/test",
        //wsUri = "ws://4.235.120.113:7070/play",
        room = "j1ycg", //Not in use for test runs
        name = "Team kOtlin",
        strategy = ::calculateAcceleration,
        joinAction = {
            logger.info(it)
        }
    )
}

fun calculateAcceleration(env: Environment, lander : Lander) : Acceleration {
    return if(lander.position.y < 200) {
        Acceleration(up = true, left = false, right = false)
    } else {
        Acceleration(up = false, left = false, right = false)
    }
}

