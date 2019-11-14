package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import ga.fundamental.integrationadapter.components.processor.AbstractMessageProcessor
import reactor.core.publisher.FluxProcessor

/**
 * Allows to choose next destination using following syntax:
 * ConditionalSplitter {
 *    { it is String } > wordMapper
 *    { it is Int && it > 100 } > bigNumberProcessor
 * }
 */
class ConditionalSplitter internal constructor(init: NextDestinationChooser.() -> Unit) : AbstractMessageProcessor() {

    internal val splitTo: MutableMap<(Message) -> Boolean, ReactiveComponent<Message>> = mutableMapOf()

    init {
        NextDestinationChooser(this).init()
    }

    override fun setEventBus(fluxProcessor: FluxProcessor<Message, Message>) {
        splitTo.values.forEach { it.setEventBus(fluxProcessor) }
        super.setEventBus(fluxProcessor)
    }

    override fun publishEvent(event: Message) {
        splitTo.filterKeys { predicate -> predicate(event) }
                .values
                .forEach { fluxProcessor.onNext(event.copy(destination = it.getOwnDestination())) }
    }

    override fun processInternal(message: Message) = message //NO-OP

}

internal class NextDestinationChooser(private val splitter: ConditionalSplitter) {
    infix operator fun ((Message) -> Boolean).compareTo(component: ReactiveComponent<Message>): Int {
        splitter.splitTo[this] = component
        return -1
    }
}
