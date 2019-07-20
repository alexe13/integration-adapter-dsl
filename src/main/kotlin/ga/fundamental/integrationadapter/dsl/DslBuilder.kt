package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.Processor
import ga.fundamental.integrationadapter.dsl.Pipeline.components
import ga.fundamental.integrationadapter.dsl.Router.pipelines


object Router {
    val pipelines: MutableMap<String, Pipeline> = HashMap()

    fun router(pipeline: Pipeline.() -> Unit) {
        Pipeline.pipeline()
        println("Pipelines: ${pipelines.keys}")
    }
}

object Pipeline {
    val components: MutableList<Pair<Processor<Message>, Processor<Message>>> = ArrayList()

    fun pipeline(name: String, component: Component.() -> Unit) {
        component(Component)
        println("Created new pipeline: $name")
        pipelines[name] = this
    }
}

object Component {
    fun link(pair: Pair<Processor<Message>, Processor<Message>>) {
        pair.first.setNextDestination(pair.second.getDestination())
        println("New link ${pair.first.getDestination()} -> ${pair.second.getDestination()}")
        components.add(pair)
    }
}
