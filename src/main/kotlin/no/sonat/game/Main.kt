package no.sonat.game

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

val logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Start client")
    val ag = AgentClient(
        wsUri = "ws://localhost:7070/test",
        room = "ignore-for-test",
        name = "Team s0nat",
        strategy = ::calculateFlight,
        joinAction = {
            logger.info(it)
        }
    )
}

val previousXError = AtomicReference(0.0)
val previousYError = AtomicReference(0.0)

fun calculateFlight( env: Environment, lander : Lander) : Acceleration {
    return if(abs(env.goal.x - lander.position.x) > 2.0) {
        val wA = 1.0
        val wB = 1.0
        val eT = (env.goal.x - lander.position.x)*wB
        val deriv = ((eT - previousXError.get())/env.constants.timeDeltaSeconds)*wA
        previousXError.set(eT)
        if(eT + deriv > 0.0) {
            Acceleration(lander.velocity.y < 0.0,left = true, right = false)
        }
        else if(eT + deriv < 0.0) {
            Acceleration(lander.velocity.y < 0.0,left = false, right =true)
        } else {
            Acceleration(lander.velocity.y < 0.0,left = false, right = false)
        }
    } else {
        val wA = 7.0
        val wB = 1.0
        val eT = (env.goal.y - lander.position.y)*wB
        val deriv = ((eT - previousYError.get())/env.constants.timeDeltaSeconds)*wA
        previousYError.set(eT)
        Acceleration(eT + deriv > 0.0,left = lander.velocity.x < 0.0,right = lander.velocity.x > 0.0)
    }
}

