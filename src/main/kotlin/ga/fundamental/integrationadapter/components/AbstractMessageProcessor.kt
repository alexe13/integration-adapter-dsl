package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageProcessor(private val componentName: String) : Processor<Message> {

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var subscribed: Boolean = false

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
        if (!subscribed) {
            subscribeToEvents()
        }
    }

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun publishEvent(event: Message) {
        fluxProcessor.onNext(event.apply { destination = nextDestinationName })
    }

    override fun subscribeToEvents() {
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .subscribe(
                        this::processAndPublish, //onNext
                        ::println              //onError
                )
    }

    private fun processAndPublish(message: Message) {
        publishEvent(processInternal(message))
    }

    abstract fun processInternal(message: Message): Message

    override fun getOwnDestination() = componentName
}