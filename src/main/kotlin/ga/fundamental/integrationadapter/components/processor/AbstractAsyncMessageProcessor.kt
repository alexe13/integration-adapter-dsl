package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import reactor.core.publisher.FluxProcessor

abstract class AbstractAsyncMessageProcessor: Processor<Message> {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractMessageProcessor::class.java)
    }

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var subscribed: Boolean = false

    private val messageSubscriber = object : Subscriber<Message> {
        override fun onComplete() {

        }

        override fun onSubscribe(s: Subscription?) {

        }

        override fun onNext(message: Message?) {
            message?.let { publishEvent(it) }
        }

        override fun onError(t: Throwable?) {
            t?.let { log.error("", it) }
        }

    }

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
                .flatMap { processAsync(it) }
                .subscribe(
                        this::publishEvent, //onNext
                        ::println              //onError
                )
    }

    abstract fun processAsync(message: Message): Publisher<Message>
}