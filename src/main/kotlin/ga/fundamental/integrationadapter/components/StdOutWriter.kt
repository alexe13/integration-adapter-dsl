package ga.fundamental.integrationadapter.components

class StdOutWriter : AbstractMessageConsumer("stdOutWriter") {

    override fun consume(message: Message) {
        println(message)
    }

}