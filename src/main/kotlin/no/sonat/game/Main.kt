package no.sonat.game

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

val logger = LoggerFactory.getLogger("Main")

fun main() {

    logger.info("Start client")

    val ag = AgentClient(
        wsUri = "ws://localhost:7070/test",
        room = "ignore-for-test",
        name = "Team s0nat",
        strategy = caclulateFlight,
        joinAction = {
            logger.info(it)
        }
    )


}

val count = AtomicLong(0)

val caclulateFlight = { _: Environment, lander : Lander ->
    val num = count.incrementAndGet()
    logger.info("State $lander")
    val left = (num/30L) % 2L == 0L
    val right = (num/30L) % 2L == 1L
    val up = lander.position.y < 250
    Acceleration(up, left, right)
}

