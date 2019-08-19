package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor


interface Component {
    fun getOwnDestination(): String
    fun setNextDestination(destinationName: String)
}

interface ReactiveComponent<T> : Component {
    fun setEventBus(fluxProcessor: FluxProcessor<T, T>)
}

interface Source<T> : ReactiveComponent<T> {
    fun publishEvent(event: T)
}

interface Sink<T> : ReactiveComponent<T> {
    fun subscribeToEvents()
}

interface Processor<T> : Source<T>, Sink<T>
