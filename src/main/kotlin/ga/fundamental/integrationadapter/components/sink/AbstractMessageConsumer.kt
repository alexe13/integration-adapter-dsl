package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Sink
import ga.fundamental.integrationadapter.components.processor.AbstractMessageProcessor
import org.slf4j.LoggerFactory
import reactor.core.publisher.FluxProcessor

abstract class AbstractMessageConsumer : Sink<Message> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractMessageProcessor::class.java)
    }

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var subscribed: Boolean = false

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        if (!subscribed) {
            this.fluxProcessor = fluxProcessor
            subscribeToEvents()
        }
    }

    override fun subscribeToEvents() {
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .subscribe(
                        ::consume, //onNext
                        { log.error("", it) }  //onError
                )
    }

    abstract fun consume(message: Message)
}