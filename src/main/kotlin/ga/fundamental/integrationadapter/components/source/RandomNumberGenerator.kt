package ga.fundamental.integrationadapter.components.source

import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

class RandomNumberGenerator : AbstractMessageSupplier() {

    private val rand = Random()

    init {
        Flux.interval(Duration.ofSeconds(3))
                .map { rand.nextInt(100) }
                .map { ga.fundamental.integrationadapter.components.Message(System.currentTimeMillis().toString(), it) }
                .subscribe(::publishEvent, ::println)
    }

    override fun getOwnDestination() = "RandomNumberGenerator#${hashCode()}"

}