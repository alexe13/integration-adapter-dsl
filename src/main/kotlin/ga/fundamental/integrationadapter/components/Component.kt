package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor


interface Component {
    fun getDestination(): String
    fun setNextDestination(destinationName: String)
}

interface ReactiveComponent<T> : Component {
    fun setEventBus(fluxProcessor: FluxProcessor<T, T>)
}