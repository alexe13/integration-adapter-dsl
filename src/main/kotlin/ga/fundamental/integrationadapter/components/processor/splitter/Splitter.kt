package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import ga.fundamental.integrationadapter.components.processor.AbstractMessageProcessor

class Splitter internal constructor(init: SplitTo.() -> Unit) : AbstractMessageProcessor() {
    internal val components: MutableSet<ReactiveComponent<Message>> = mutableSetOf()

    init {
        SplitTo(this).init()
    }

    override fun publishEvent(event: Message) {
        components.forEach {
            it.setEventBus(fluxProcessor)
            fluxProcessor.onNext(event.copy(destination = it.getOwnDestination()))
        }
    }

    override fun processInternal(message: Message) = message //NO-OP
}

@SplitterDSL
internal class SplitTo(private val splitter: Splitter) {
    operator fun ReactiveComponent<Message>.unaryMinus() {
        splitter.components.add(this)
    }
}

@DslMarker
annotation class SplitterDSL

