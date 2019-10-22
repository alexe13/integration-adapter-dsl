package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message

class StdErrWriter : AbstractMessageConsumer() {

    override fun consume(message: Message) {
        System.err.println("[${Thread.currentThread().name}] ${getOwnDestination()} -> $message")
    }

}