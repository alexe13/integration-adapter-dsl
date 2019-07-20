package ga.fundamental.integrationadapter.components

class SimpleMapper : AbstractFluxProcessor() {

    override fun getDestination() = "simpleMapper"

    override fun processInternal(message: Message): Message {
        return message.copy(
                payload = if (message.payload is String) {
                    message.payload.map { it.toUpperCase() }
                } else {
                    message.payload
                }
        )
    }
}