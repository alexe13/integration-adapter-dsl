package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor

class StdOutReader : Source<Message> {
    private lateinit var nextDestinationName: String

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>

    init {
        Thread {
            while (true) {
            readLine()?.let {
                publishEvent(Message(System.currentTimeMillis().toString(), it))
            }}
        }.start()
    }

    override fun getOwnDestination(): String = "stdOutReader"

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