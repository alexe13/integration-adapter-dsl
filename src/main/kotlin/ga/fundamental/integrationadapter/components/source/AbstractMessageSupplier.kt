package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Source
import org.springframework.beans.factory.BeanNameAware
import reactor.core.publisher.FluxProcessor
import java.util.*

abstract class AbstractMessageSupplier : Source<Message>, BeanNameAware {
    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var beanName: String = javaClass.simpleName
    private val ownId = UUID.randomUUID().toString()

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        this.fluxProcessor = fluxProcessor
    }

    override fun publishEvent(event: Message) {
        fluxProcessor.onNext(event.apply { destination = nextDestinationName })
    }

    override fun getOwnDestination() = "$beanName($ownId)"

    override fun setBeanName(name: String) {
        beanName = name
    }
}