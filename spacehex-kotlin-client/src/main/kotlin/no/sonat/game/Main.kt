package no.sonat.game

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

val logger = LoggerFactory.getLogger("Main")

fun main() {
    logger.info("Start client")
    val ag = AgentClient(
        wsUri = "ws://localhost:7070/test",
        room = "6dnxu",
        name = "Team kOtlin",
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
        val weightA = 1.0
        val weightB = 1.0
        val eT = (env.goal.x - lander.position.x)*weightB
        val deriv = ((eT - previousXError.get())/env.constants.timeDeltaSeconds)*weightA
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
        val weightA = 7.0
        val weightB = 1.0
        val eT = (env.goal.y - lander.position.y)*weightB
        val deriv = ((eT - previousYError.get())/env.constants.timeDeltaSeconds)*weightA
        previousYError.set(eT)
        Acceleration(eT + deriv > 0.0,left = lander.velocity.x < 0.0,right = lander.velocity.x > 0.0)
    }
}

