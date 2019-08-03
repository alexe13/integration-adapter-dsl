package ga.fundamental.integrationadapter

import ga.fundamental.integrationadapter.components.*
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.ReplayProcessor

@SpringBootApplication
class IntegrationAdapterApplication

val stdOutReader = StdOutReader()
val simpleMapper = SimpleMapper()
val letterCounter = LetterCounter()
val stdOutWriter = StdOutWriter()
val simpleMapper2 = SimpleMapper()
val letterCounter2 = LetterCounter()

fun main(args: Array<String>) {
    runApplication<IntegrationAdapterApplication>(*args)
    val replayProcessor = ReplayProcessor.create<Message>()


    Router {
        pipeline("Count letters from console input") {
            eventBus(replayProcessor)
            components {
                link(stdOutReader to simpleMapper)
                link(simpleMapper to letterCounter)
                link(letterCounter to stdOutWriter)
            }
        }
        pipeline("Some other processing pipeline") {
            components {
                link(simpleMapper2 to letterCounter2)
            }
        }
    }
}
