package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Source
import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageSupplier : Source<Message> {
    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
    }

    override fun publishEvent(event: Message) {
        fluxProcessor.onNext(event.apply { destination = nextDestinationName })
    }

}