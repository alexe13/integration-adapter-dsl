package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message

class ErrorAccumulator: AbstractMessageProcessor() {

    private val errorsStore: MutableList<Any> = mutableListOf()

    override fun processInternal(message: Message): Message {
        errorsStore.add(message.payload)
        return message.copy(payload = errorsStore)
    }

}