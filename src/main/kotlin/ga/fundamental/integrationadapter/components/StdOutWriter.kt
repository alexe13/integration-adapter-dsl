package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

class StdOutWriter : Sink<Message> {
    private lateinit var nextDestinationName: String

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>

    private var subscribed: Boolean = false

    override fun getOwnDestination(): String = "stdOutWriter"

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
                        { println("stdOutWriter: $it") }, //onNext
                        ::println  //onError
                )
    }

}