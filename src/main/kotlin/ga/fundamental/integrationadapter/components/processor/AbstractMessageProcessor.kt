package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import org.springframework.beans.factory.BeanNameAware
import reactor.core.publisher.FluxProcessor
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.*

abstract class AbstractMessageProcessor : Processor<Message>, BeanNameAware {

    protected lateinit var fluxProcessor: FluxProcessor<Message, Message>
    private lateinit var nextDestinationName: String
    private var scheduler = Schedulers.immediate()
    private var beanName: String = javaClass.simpleName
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

    override fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }

    override fun publishEvent(event: Message) {
        fluxProcessor.onNext(event.apply { destination = nextDestinationName })
    }

    override fun subscribeToEvents() {
        subscribed = true
        fluxProcessor.filter { it != null }
                .filter { it.destination == getOwnDestination() }
                .onErrorContinue { throwable, _ -> System.err.println(throwable) }
                .subscribeOn(scheduler)
                .subscribe(
                        this::processAndPublish, //onNext
                        ::println              //onError
                )
    }

    private fun processAndPublish(message: Message) {
        publishEvent(processInternal(message))
    }

    override fun setBeanName(name: String) {
        beanName = name
    }

    abstract fun processInternal(message: Message): Message
}