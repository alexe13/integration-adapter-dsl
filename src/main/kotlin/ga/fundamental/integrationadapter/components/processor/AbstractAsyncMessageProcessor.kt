package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanNameAware
import reactor.core.publisher.FluxProcessor
import java.util.*

abstract class AbstractAsyncMessageProcessor : Processor<Message>, BeanNameAware {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractMessageProcessor::class.java)
    }

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private lateinit var beanName: String
    private var subscribed: Boolean = false
    private val ownId = UUID.randomUUID().toString()

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
        if (!subscribed) {
            subscribeToEvents()
        }
    }

    override fun getOwnDestination() = "$beanName($ownId)"

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

    override fun setBeanName(name: String) {
        beanName = name
    }
}