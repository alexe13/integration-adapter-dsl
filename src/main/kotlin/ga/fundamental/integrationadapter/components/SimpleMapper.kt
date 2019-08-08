package ga.fundamental.integrationadapter.components

class SimpleMapper : AbstractMessageProcessor("simpleMapper") {

    override fun processInternal(message: Message): Message {
        return message.copy(
                payload = if (message.payload is String) {
                    message.payload.toUpperCase()
                } else {
                    message.payload
                }
        )
    }
}