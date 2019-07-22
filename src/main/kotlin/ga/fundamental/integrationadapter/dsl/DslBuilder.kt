package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import ga.fundamental.integrationadapter.components.Sink
import ga.fundamental.integrationadapter.dsl.Pipeline.components
import ga.fundamental.integrationadapter.dsl.Router.pipelines
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.FluxProcessor


object Router {
    val pipelines: MutableMap<String, Pipeline> = HashMap()

    fun router(pipeline: Pipeline.() -> Unit) {
        Pipeline.pipeline()
        pipelines.values.flatMap { it.components }
                .flatMap { it.toList() }
                .distinct()
                .forEach {
                    if (it is Sink<*>) {
                        it.subscribeToEvents()
                    }
                }
        println("Pipelines: ${pipelines.keys}")
    }
}

object Pipeline {
    val components: MutableList<Pair<ReactiveComponent<Message>, ReactiveComponent<Message>>> = ArrayList()

    fun pipeline(name: String, component: Component.() -> Unit) {
        val eventBus: FluxProcessor<Message, Message> = EmitterProcessor.create()
        component(Component)
        components.map { (f,s) -> f.apply { setEventBus(eventBus); s.apply { setEventBus(eventBus) } } }
        println("Created new pipeline: $name")
        pipelines[name] = this
    }
}

object Component {
    fun link(pair: Pair<ReactiveComponent<Message>, ReactiveComponent<Message>>) {
        pair.first.setNextDestination(pair.second.getDestination())
        println("New link ${pair.first.getDestination()} -> ${pair.second.getDestination()}")
        components.add(pair)
    }
}
