package ga.fundamental.integrationadapter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.ReactiveComponent
import reactor.core.publisher.FluxProcessor

fun ReactiveComponent<Message>.configure(testBus: FluxProcessor<Message, Message>? = null, nextDestination: String? = null): String {
    testBus?.let { this.setEventBus(it) }
    nextDestination?.let { this.setNextDestination(it) }
    return this.getOwnDestination()
}