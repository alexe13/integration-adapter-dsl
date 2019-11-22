package ga.fundamental.integrationadapter.dsl.bus

import ga.fundamental.integrationadapter.components.Message
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers


class DefaultRouterBus(
        private val scheduler: Scheduler = Schedulers.immediate(),
        private val bufferSize: Int = Int.MAX_VALUE) : RouterBus<Message> {

    private val bus: DirectProcessor<Message> = DirectProcessor.create()
    private val sink: FluxSink<Message> = bus.sink(FluxSink.OverflowStrategy.BUFFER)

    override fun get(): Flux<Message> = bus.onBackpressureBuffer(bufferSize).publishOn(scheduler)

    override fun publish(data: Message) {
        sink.next(data)
    }

}