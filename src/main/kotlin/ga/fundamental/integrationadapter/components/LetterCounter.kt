package ga.fundamental.integrationadapter.components

class LetterCounter : AbstractMessageProcessor("letterCounter") {

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