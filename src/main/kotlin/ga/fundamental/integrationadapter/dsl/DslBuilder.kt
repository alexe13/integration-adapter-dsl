package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.CanConsumeEvents
import ga.fundamental.integrationadapter.components.CanPushEvents
import ga.fundamental.integrationadapter.components.Message
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.ReplayProcessor
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

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
    internal val components: MutableList<Link<CanPushEvents<Message>, CanConsumeEvents<Message>>> = ArrayList()
    internal var scheduler: Scheduler = Schedulers.single()

    fun eventBus(eventBus: FluxProcessor<Message, Message>) {
        this.eventBus = eventBus
    }

    fun scheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }

    fun components(init: Component.() -> Unit) {
        initComponents(this, init)

        components.map { link ->
            link.pair.toList().map {
                it.apply { setEventBus(eventBus) }
                it.apply { setScheduler(scheduler) }
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
    fun link(pair: Pair<CanPushEvents<Message>, CanConsumeEvents<Message>>): Link<CanPushEvents<Message>, CanConsumeEvents<Message>> {
        pair.first.setNextDestination(pair.second.getOwnDestination())
        val link = Link(pair)
        pipeline.components.add(link)
        println("New link ${link.pair.first.getOwnDestination()} -> ${link.pair.second.getOwnDestination()}")
        return link
    }
}

@RouterDslScope
data class Link<F : CanPushEvents<*>, S : CanConsumeEvents<*>>(val pair: Pair<F, S>)

/**
 * Controls DSL operator's scope to prohibit repeated outer receiver usage
 **/
@DslMarker
annotation class RouterDslScope

fun Router.destroy() {
    this.pipelines.clear()
}
