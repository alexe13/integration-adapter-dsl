package ga.fundamental.integrationadapter.components.sink

import ga.fundamental.integrationadapter.components.BaseActor
import ga.fundamental.integrationadapter.components.Message
import kotlinx.coroutines.channels.ActorScope

abstract class MessageConsumingActor(name: String) : BaseActor<Message>(name) {

    override suspend fun coroutineLogic(scope: ActorScope<Message>) {
        for (message in scope.channel) {
            consume(message)
        }
    }

    abstract fun consume(message: Message)
}