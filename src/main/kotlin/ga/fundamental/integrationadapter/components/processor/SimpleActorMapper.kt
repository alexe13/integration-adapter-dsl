package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message

class SimpleActorMapper(name: String, private val mappingFunction: (Message) -> Message) : MessageProcessingActor(name) {

    override fun process(message: Message) = mappingFunction(message)
}