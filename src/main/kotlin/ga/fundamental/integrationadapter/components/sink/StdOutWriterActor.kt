package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.Message

class StdOutWriterActor(name: String) : MessageConsumingActor(name) {

    override fun consume(message: Message) {
        println("[${System.nanoTime()}] -[${Thread.currentThread().name}] $name -> $message")
    }

}