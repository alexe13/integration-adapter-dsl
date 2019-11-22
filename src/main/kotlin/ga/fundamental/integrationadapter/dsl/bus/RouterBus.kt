package ga.fundamental.integrationadapter.dsl.bus

import reactor.core.publisher.Flux

interface RouterBus<T> {

    fun get(): Flux<T>

    fun publish(data: T)

}