package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.ConditionalMessageSubscriber
import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageProcessor(var predicate: (Message) -> Boolean = { true }) : Processor<Message>, ConditionalMessageSubscriber {

    override var condition: (Message) -> Boolean
        get() = predicate
        set(value) {predicate = value}
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
                .filter(predicate)
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
}