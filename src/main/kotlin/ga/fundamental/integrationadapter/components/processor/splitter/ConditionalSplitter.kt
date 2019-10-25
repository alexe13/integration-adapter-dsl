package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import ga.fundamental.integrationadapter.components.processor.AbstractMessageProcessor

class ConditionalSplitter(private val nextDestinationChooser: (Message) -> ReactiveComponent<Message>) : AbstractMessageProcessor() {

    override fun publishEvent(event: Message) {
        val nextComponent = nextDestinationChooser(event)
        nextComponent.setEventBus(fluxProcessor)
        val event2 = event.copy(destination = nextComponent.getOwnDestination())
        fluxProcessor.onNext(event2)
    }

    override fun processInternal(message: Message) = message //NO-OP

}
