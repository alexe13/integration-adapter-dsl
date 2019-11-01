package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Sink
import ga.fundamental.integrationadapter.components.processor.AbstractMessageProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanNameAware
import reactor.core.publisher.FluxProcessor
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.*

abstract class AbstractMessageConsumer : Sink<Message>, BeanNameAware {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractMessageProcessor::class.java)
    }

    private lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var scheduler: Scheduler = Schedulers.single()
    private var beanName: String = javaClass.simpleName
    private var subscribed: Boolean = false
    private val ownId = UUID.randomUUID().toString()

    override fun setNextDestination(destinationName: String) {
        this.nextDestinationName = destinationName
    }

    override fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        if (!subscribed) {
            this.fluxProcessor = fluxProcessor
            subscribeToEvents()
        }
    }

    override fun getOwnDestination() = "$beanName($ownId)"

    override fun subscribeToEvents() {
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .subscribeOn(scheduler)
                .subscribe(
                        ::consume, //onNext
                        { log.error("", it) }  //onError
                )
    }

    override fun setBeanName(name: String) {
        beanName = name
    }

    abstract fun consume(message: Message)
}