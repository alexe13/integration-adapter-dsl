package ga.fundamental.integrationadapter.components

import reactor.core.publisher.FluxProcessor
import reactor.core.scheduler.Scheduler


/**
 * Basic router component
 */
interface BaseComponent {
    /**
     * Should return this component's unique identifier.
     * If identifiers are not unique, same message will be processed by multiple components at once
     */
    fun getOwnDestination(): String

    /**
     * Should return unique identifier of next component in a pipeline
     * Linking is done by Router DSL
     */
    fun setNextDestination(destinationName: String)
}

/**
 * Basic component that knows about Reactor-specific event buses and schedulers
 */
interface ReactiveComponent<T> : BaseComponent {
    /**
     * Set a Reactor [Scheduler] to this component
     * Component is required to perform all processing in a context of this scheduler
     */
    fun setScheduler(scheduler: Scheduler)

    /**
     * Set a Reactor [FluxProcessor] to this component
     * Component is required to publish/subscribe exclusively to this event bus
     * Components in a single pipeline are connected by a common event bus
     */
    fun setEventBus(fluxProcessor: FluxProcessor<T, T>)
}

interface CanPushEvents<T>: ReactiveComponent<T>

interface CanConsumeEvents<T>: ReactiveComponent<T>

/**
 * Source of events
 */
interface Source<T> : CanPushEvents<T> {
    /**
     * Component is required to publish events to its own event bus
     */
    fun publishEvent(event: T)
}

/**
 * Consumer of events
 */
interface Sink<T> : CanConsumeEvents<T> {
    /**
     * Component is required to subscribe to its own event bus
     */
    fun subscribeToEvents()
}

/**
 * Component, that both subscribes to new events and publishes them
 * (read -> process -> publish further)
 */
interface Processor<T> : Source<T>, Sink<T>
