package ga.fundamental.integrationadapter.components.source

import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

class RandomNumberGenerator(emissionFrequency: Duration = Duration.ofSeconds(5)) : AbstractMessageSupplier() {

    private val rand = Random()

    init {
        Flux.interval(emissionFrequency)
                .map { rand.nextInt(120) }
                .map { ga.fundamental.integrationadapter.components.Message(System.currentTimeMillis().toString(), it) }
                .subscribe(::publishEvent, ::println)
    }

}