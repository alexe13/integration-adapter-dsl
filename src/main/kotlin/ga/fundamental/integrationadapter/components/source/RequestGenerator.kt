package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

class RequestGenerator(requestFrequency: Duration) : AbstractMessageSupplier() {

    private val requestNumber = AtomicLong()

    init {
        Flux.interval(requestFrequency)
                .map { Message(System.nanoTime().toString(), requestNumber.incrementAndGet().toString()) }
                .log("RequestGenerator")
                .subscribe(::publishEvent, ::println)
    }

    override fun getOwnDestination() = "RequestGenerator#${hashCode()}"
}