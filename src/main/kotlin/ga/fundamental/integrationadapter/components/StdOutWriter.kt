package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

class StdOutWriter : Sink<Message> {
    private lateinit var nextDestinationName: String

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>

    override fun getDestination(): String = "stdOutWriter"

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
    }

    override fun subscribeToEvents() {
        fluxProcessor.filter { it != null }
                .filter { it.destination == getDestination() }
                .subscribe(
                        { println("stdOutWriter: $it") }, //onNext
                        ::println  //onError
                )
    }

}