package ga.fundamental.integrationadapter.components.source

import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

class RandomNumberActor(name: String, emissionFrequency: Duration = Duration.ofSeconds(5)) : MessageProducingActor(name) {

    private val rand = Random()

    init {
        Flux.interval(emissionFrequency)
                .map { rand.nextInt(100) }
                .map { ga.fundamental.integrationadapter.components.Message(System.currentTimeMillis().toString(), it) }
                .subscribe(::send, ::println)
    }
}