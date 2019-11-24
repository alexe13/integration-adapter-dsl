package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicLong

class RequestGenerator(val requestAmount: Int) : AbstractMessageSupplier() {

    private val requestNumber = AtomicLong()

    fun start() {
        Flux.range(0, requestAmount)
                .map { Message(System.nanoTime().toString(), requestNumber.incrementAndGet()) }
                .subscribe(::publishEvent, ::println)
    }

}