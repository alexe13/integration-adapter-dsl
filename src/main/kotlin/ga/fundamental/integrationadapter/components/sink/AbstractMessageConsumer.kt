package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Sink
import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageConsumer : Sink<Message> {

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
}