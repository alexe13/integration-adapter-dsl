package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message

class StdOutWriter : AbstractMessageConsumer() {
    override fun getOwnDestination() = "StdOutWriter#${hashCode()}"

    override fun consume(message: Message) {
        println("${getOwnDestination()} -> $message")
    }

}