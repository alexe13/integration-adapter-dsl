package ga.fundamental.integrationadapter.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor

abstract class BaseActor<T>(internal val name: String) : Actor<T>, CoroutineScope by CoroutineScope(Dispatchers.IO) {
    internal val recipients = mutableListOf<Actor<T>>()

    private val actor = actor<T>(capacity = Channel.UNLIMITED) {

        coroutineLogic(this)

    }

    abstract suspend fun coroutineLogic(scope: ActorScope<T>)

    override fun pendingSize(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addRecipients(vararg actors: Actor<T>) {
        recipients += actors
    }

    override fun send(message: T) {
        actor.offer(message)
    }

    override fun getName() = name
}