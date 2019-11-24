package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.BaseActor
import ga.fundamental.integrationadapter.components.Message
import kotlinx.coroutines.channels.ActorScope

abstract class MessageProducingActor(name: String) : BaseActor<Message>(name) {

    override suspend fun coroutineLogic(scope: ActorScope<Message>) {
        for (message in scope.channel) {
            recipients.forEach { it.send(message) }
        }
    }
}