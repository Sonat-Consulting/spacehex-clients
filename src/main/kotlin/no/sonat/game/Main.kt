package no.sonat.game

import org.slf4j.LoggerFactory

fun main() {

    val logger = LoggerFactory.getLogger("Main")

    logger.info("Start client")

    val ag = AgentClient(
        wsUri = "ws://localhost:7070/test",
        room = "",
        name = "yolo",
        strategy = { Acceleration(false,false,false) },
        joinAction = {logger.info(it)}
    )


}

