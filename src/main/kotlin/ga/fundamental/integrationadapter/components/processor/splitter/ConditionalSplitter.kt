package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import ga.fundamental.integrationadapter.components.ReactiveComponent
import reactor.core.publisher.FluxProcessor

class ConditionalSplitter(private val nextDestinationChooser: (Message) -> ReactiveComponent<Message>) : Processor<Message> {
    private var subscribed = false
    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String

    override fun getOwnDestination() = "ConditionalSplitter#${hashCode()}"

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
        if (!subscribed) {
            subscribeToEvents()
        }
    }

    override fun publishEvent(event: Message) {
        val nextComponent = nextDestinationChooser(event)
        nextComponent.setEventBus(fluxProcessor)
        val event2 = event.copy(destination = nextComponent.getOwnDestination())
        fluxProcessor.onNext(event2)
    }

    override fun subscribeToEvents() {
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .onErrorContinue { throwable, _ -> System.err.println(throwable) }
                .subscribe(
                        this::publishEvent,
                        ::println
                )
    }
}
