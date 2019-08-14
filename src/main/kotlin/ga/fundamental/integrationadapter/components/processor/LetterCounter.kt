package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message

class LetterCounter : AbstractMessageProcessor() {
    override fun getOwnDestination() = "LetterCounter#${hashCode()}"

    override fun processInternal(message: Message): Message {
        return message.copy(
                payload = if (message.payload is String) {
                    message.payload.count()
                } else {
                    message.payload
                }
        )
    }
}