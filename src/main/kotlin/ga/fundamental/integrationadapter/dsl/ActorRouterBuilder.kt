package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Actor
import ga.fundamental.integrationadapter.components.Message


object ActorRouter {
    val pipelines: MutableMap<String, ActorPipeline> = HashMap()

    operator fun invoke(init: ActorPipelines.() -> Unit): ActorRouter {
        ActorPipelines().init()
        println("Pipelines: ${pipelines.keys}")
        return this
    }
}

class ActorPipelines {
    fun pipeline(name: String, init: ActorPipeline.() -> Unit) {
        val pipeline = ActorPipeline(name)
        pipeline.init()
        ActorRouter.pipelines[pipeline.name] = pipeline
    }
}

class ActorPipeline(val name: String) {
    internal val components: MutableList<ActorLink> = ArrayList()

    fun components(init: ActorComponent.() -> Unit) {
        initComponents(this, init)
    }

    private fun initComponents(pipeline: ActorPipeline, init: ActorComponent.() -> Unit) {
        val comp = ActorComponent(pipeline)
        comp.init()
    }
}

@RouterDslScope
class ActorComponent(private val pipeline: ActorPipeline) {
    fun link(pair: Pair<Actor<Message>, Actor<Message>>): ActorLink {
        pair.first.addRecipients(pair.second)
        val link = ActorLink(pair)
        pipeline.components.add(link)
        println("New link ${link.pair.first.getName()} -> ${link.pair.second.getName()}")
        return link
    }
}

data class ActorLink(val pair: Pair<Actor<Message>, Actor<Message>>)

fun ActorRouter.destroy() {
    this.pipelines.clear()
}