package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.ReplayProcessor

@RouterDslScope
object Router {
    val pipelines: MutableMap<String, Pipeline> = HashMap()

    operator fun invoke(init: Pipelines.() -> Unit): Router {
        Pipelines().init()
        println("Pipelines: ${pipelines.keys}")
        return this
    }
}

@RouterDslScope
class Pipelines {
    fun pipeline(name: String, init: Pipeline.() -> Unit) {
        val pipeline = Pipeline(name)
        pipeline.init()
        Router.pipelines[pipeline.name] = pipeline
    }
}

@RouterDslScope
class Pipeline(val name: String) {
    internal var eventBus: FluxProcessor<Message, Message> = ReplayProcessor.create(1)
    internal val components: MutableList<Link<ReactiveComponent<Message>>> = ArrayList()

    fun eventBus(eventBus: FluxProcessor<Message, Message>) {
        this.eventBus = eventBus
    }

    fun components(init: Component.() -> Unit) {
        initComponents(this, init)

        components.map { link ->
            link.pair.toList().map {
                it.apply { setEventBus(eventBus) }
            }
        }
        println("Created new pipeline: $name, eventBus: $eventBus#${eventBus.hashCode()}")
    }

    private fun initComponents(pipeline: Pipeline, init: Component.() -> Unit) {
        val comp = Component(pipeline)
        comp.init()
    }
}

@RouterDslScope
class Component(private val pipeline: Pipeline) {
    fun link(pair: Pair<ReactiveComponent<Message>, ReactiveComponent<Message>>): Link<ReactiveComponent<Message>> {
        pair.first.setNextDestination(pair.second.getOwnDestination())
        val link = Link(pair)
        pipeline.components.add(link)
        println("New link ${link.pair.first.getOwnDestination()} -> ${link.pair.second.getOwnDestination()}")
        return link
    }
}

@RouterDslScope
data class Link<T>(val pair: Pair<T, T>)

/**
 * Controls DSL operator's scope to prohibit repeated outer receiver usage
 **/
@DslMarker
annotation class RouterDslScope
