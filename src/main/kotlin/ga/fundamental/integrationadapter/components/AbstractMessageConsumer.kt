package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageConsumer(private val componentName: String) : Sink<Message> {

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var subscribed: Boolean = false

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
        if (!subscribed) {
            subscribeToEvents()
        }
    }

    override fun subscribeToEvents() {
        println("${getOwnDestination()} subscribes to eventBus ${fluxProcessor.hashCode()}")
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .subscribe(
                        ::consume, //onNext
                        ::println  //onError
                )
    }

    abstract fun consume(message: Message)

    override fun getOwnDestination() = componentName
}