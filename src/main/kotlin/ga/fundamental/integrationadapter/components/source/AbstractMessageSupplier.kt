package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Source
import org.springframework.beans.factory.BeanNameAware
import reactor.core.publisher.FluxProcessor
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.*

abstract class AbstractMessageSupplier : Source<Message>, BeanNameAware {
    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var scheduler: Scheduler = Schedulers.single()
    private var beanName: String = javaClass.simpleName
    private val ownId = UUID.randomUUID().toString()

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
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