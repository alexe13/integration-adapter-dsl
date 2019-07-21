package ga.fundamental.integrationadapter

import ga.fundamental.integrationadapter.components.LetterCounter
import ga.fundamental.integrationadapter.components.SimpleMapper
import ga.fundamental.integrationadapter.components.StdOutReader
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IntegrationAdapterApplication

val reader1 = StdOutReader()
val processor2 = LetterCounter()
val processor3 = SimpleMapper()
val processor4 = LetterCounter()

fun main(args: Array<String>) {
    runApplication<IntegrationAdapterApplication>(*args)

    Router.router {
        pipeline("CB courses") {
            link(reader1 to processor2)
            link(processor2 to processor3)
            link(processor3 to processor4)
        }
        pipeline("RFQ request") {
            link(processor4 to processor2)
        }
    }
}
