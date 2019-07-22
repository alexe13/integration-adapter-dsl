package ga.fundamental.integrationadapter

import ga.fundamental.integrationadapter.components.LetterCounter
import ga.fundamental.integrationadapter.components.SimpleMapper
import ga.fundamental.integrationadapter.components.StdOutReader
import ga.fundamental.integrationadapter.components.StdOutWriter
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IntegrationAdapterApplication

val stdOutReader = StdOutReader()
val simpleMapper = SimpleMapper()
val letterCounter = LetterCounter()
val stdOutWriter = StdOutWriter()

fun main(args: Array<String>) {
    runApplication<IntegrationAdapterApplication>(*args)

    Router.router {
        pipeline("Count letters from console input") {
            link(stdOutReader to simpleMapper)
            link(simpleMapper to letterCounter)
            link(letterCounter to stdOutWriter)
        }
        pipeline("Some other processing pipeline") {
            link(simpleMapper to letterCounter)
        }
    }
}
