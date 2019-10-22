package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message

class SimpleMapper(private val mappingFunction: (Message) -> Message) : AbstractMessageProcessor() {

    override fun processInternal(message: Message): Message {
        return mappingFunction(message)
    }
}