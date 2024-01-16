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

val previousXError = AtomicReference(0.0)
val previousYError = AtomicReference(0.0)

fun calculateFlight( env: Environment, lander : Lander) : Acceleration {
    return if(abs(env.goal.x - lander.position.x) > 2.0) {
        val weightAx = 4.0
        val weightBx = 1.0
        val eT = (env.goal.x - lander.position.x)*weightBx
        val deriv = ((eT - previousXError.get())/env.constants.timeDeltaSeconds)*weightAx
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
        val weightAx = 2.0
        val weightBx = 1.0
        val eTx = (env.goal.x - lander.position.x)*weightBx
        val derivX = ((eTx - previousXError.get())/env.constants.timeDeltaSeconds)*weightAx
        previousXError.set(eTx)
        val left = eTx + derivX > 0.0
        val right = eTx + derivX < 0.0

        val weightA = 7.0
        val weightB = 1.0
        val eT = (env.goal.y - lander.position.y)*weightB
        val deriv = ((eT - previousYError.get())/env.constants.timeDeltaSeconds)*weightA
        previousYError.set(eT)
        Acceleration(eT + deriv > 0.0,left = left,right = right)
    }
}

