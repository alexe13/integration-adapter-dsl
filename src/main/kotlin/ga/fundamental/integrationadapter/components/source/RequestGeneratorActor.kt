package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message

class RequestGeneratorActor(name: String, private val requestAmount: Int = 100) : MessageProducingActor(name) {

    fun start() {
        for (i in 0..requestAmount) {
            send(Message(System.nanoTime().toString(), i))
        }
    }

}