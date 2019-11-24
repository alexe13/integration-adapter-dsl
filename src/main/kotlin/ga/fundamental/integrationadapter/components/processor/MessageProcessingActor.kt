package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.BaseActor
import ga.fundamental.integrationadapter.components.Message
import kotlinx.coroutines.channels.ActorScope

abstract class MessageProcessingActor(name: String) : BaseActor<Message>(name) {

    override suspend fun coroutineLogic(scope: ActorScope<Message>) {

        for (message in scope.channel) {
            recipients.forEach { it.send(process(message)) }
        }

    }

    abstract fun process(message: Message): Message
}