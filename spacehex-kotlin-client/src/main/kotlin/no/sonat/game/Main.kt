package no.sonat.game

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

val logger: Logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Start client")
    val ag = AgentClient(
        wsUri = "ws://51.120.245.215:7070/test",
        //wsUri = "ws://51.120.245.215:7070/play",
        room = "j1ycg", //Not in use for test runs
        name = "Team kOtlin",
        strategy = ::calculateFlight,
        joinAction = {
            logger.info(it)
        }
    )
}

fun calculateFlight( env: Environment, lander : Lander) : Acceleration {
    return if(lander.position.y < 200) {
        Acceleration(up = true,left = false, right = false)
    } else {
        Acceleration(up = false, left = false, right = false)
    }
}

