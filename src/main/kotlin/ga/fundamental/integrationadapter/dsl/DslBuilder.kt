package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import ga.fundamental.integrationadapter.dsl.Router.pipelines
import ga.fundamental.integrationadapter.dsl.Pipeline.components


object Router {
    val pipelines: MutableMap<String, Pipeline> = HashMap()

    fun router(pipeline: Pipeline.() -> Unit) {
        Pipeline.pipeline()
        println("Pipelines: $pipelines")
    }

    fun start() {

    }
}

object Pipeline {
    val components: MutableList<Pair<Processor<Message>, Processor<Message>>> = ArrayList()

    fun pipeline(name: String, component: Component.() -> Unit) {
        component(Component)
        println("Created new pipeline: $name")
        pipelines[name] = this
    }

    fun start() {
        components.forEach { it.first.next(it.second) }
    }
}

object Component {
    fun link(pair: Pair<Processor<Message>, Processor<Message>>) {
        println("New link ${pair.first} -> ${pair.second}")
        components.add(pair)
    }
}
