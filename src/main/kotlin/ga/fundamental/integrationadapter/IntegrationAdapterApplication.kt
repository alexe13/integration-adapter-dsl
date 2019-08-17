package ga.fundamental.integrationadapter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.LetterCounter
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.components.source.StdOutReader
import ga.fundamental.integrationadapter.dsl.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.ReplayProcessor

@SpringBootApplication
class IntegrationAdapterApplication

val stdOutReader = StdOutReader()
val simpleMapper = SimpleMapper {
    when (val payloadNumber = it.payload as Int) {
        in 0..30 -> Message(it.id, "Pretty small number: $payloadNumber")
        in 30..60 -> Message(it.id, "Decent number: $payloadNumber")
        in 60..100 -> Message(it.id, "Big number: $payloadNumber")
        else -> Message(it.id, "Huh?")
    }
}
val letterCounter = LetterCounter()
val stdOutWriter = StdOutWriter()
val stdOutWriter2 = StdOutWriter()
val randomGenerator = RandomNumberGenerator()

fun main(args: Array<String>) {
    runApplication<IntegrationAdapterApplication>(*args)
    val replayProcessor = ReplayProcessor.create<Message>()


    Router {
        pipeline("Count letters from console input") {
            eventBus(replayProcessor)
            components {
                link(stdOutReader to letterCounter)
                link(letterCounter to stdOutWriter)
            }
        }
        pipeline("Generate random numbers") {
            components {
                link(randomGenerator to simpleMapper) `on condition that` { it.payload is Int && it.payload > 50 }
                link(simpleMapper to stdOutWriter2)
            }
        }
    }
}
