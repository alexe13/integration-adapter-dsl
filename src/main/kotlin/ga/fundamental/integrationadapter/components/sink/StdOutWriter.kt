package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message

class StdOutWriter : AbstractMessageConsumer() {

    override fun consume(message: Message) {
        println("[${System.nanoTime()}] - [${Thread.currentThread().name}] ${getOwnDestination()} -> $message")
    }

}