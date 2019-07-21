package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

abstract class AbstractFluxProcessor : Processor<Message> {

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>

    private lateinit var nextDestinationName: String

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
    }

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun publishEvent(event: Message) {
        fluxProcessor.onNext(event.apply { destination = nextDestinationName })
    }

    override fun subscribeToEvents() {
        fluxProcessor.filter { it != null }
                .filter { it.destination == getDestination() }
                .subscribe(
                        this::processAndPublish, //onNext
                        ::println              //onError
                )
    }

    private fun processAndPublish(message: Message) {
        publishEvent(processInternal(message))
    }

    abstract fun processInternal(message: Message): Message

}