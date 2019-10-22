package ga.fundamental.integrationadapter.config

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.LetterCounter
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.processor.splitter.ConditionalSplitter
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.components.source.StdOutReader
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.ReplayProcessor

@Profile("crossPipe")
@Configuration
class CrossPipeRouterConfiguration {

    @Bean
    fun router(): Router {
        return Router {
            pipeline("Count letters from console input") {
//                eventBus(randomNumbersPipelineEventBus())
                components {
                    link(stdOutReader() to longMessageSplitter()) //publishes to another pipeline
                    link(letterCounter() to stdOutWriter())
                }
            }
            pipeline("Generate random numbers") {
//                eventBus(randomNumbersPipelineEventBus())
                components {
                    link(randomNumberGenerator() to randomNumberSplitter())
                    link(randomNumberMapper() to stdOutWriter2())
                }
            }
        }
    }

    @Bean
    fun randomNumbersPipelineEventBus() = ReplayProcessor.create<Message>(1)

    @Bean
    fun stdOutReader() = StdOutReader()

    @Bean
    fun randomNumberMapper() = SimpleMapper {
        when (val payloadNumber = it.payload as Double) {
            in 0..30 -> Message(it.id, "Pretty small number: $payloadNumber")
            in 30..60 -> Message(it.id, "Decent number: $payloadNumber")
            in 60..100 -> Message(it.id, "Big number: $payloadNumber")
            else -> Message(it.id, "Huh?")
        }
    }

    @Bean
    fun longMessageSplitter() = ConditionalSplitter {
        when((it.payload as String).length) {
            in 0..10 -> letterCounter()
            else -> stdErrWriter()
        }
    }

    @Bean
    fun randomNumberSplitter() = ConditionalSplitter {
        when {
            it.payload is Double && it.payload <= 100 -> randomNumberMapper()
            it.payload is Double && it.payload > 100 -> stdErrWriter()
            else -> throw IllegalArgumentException("Unexpected payload: ${it.payload}")
        }
    }

    @Bean
    fun letterCounter() = LetterCounter()

    @Bean
    fun stdOutWriter() = StdOutWriter()

    @Bean
    fun stdOutWriter2() = StdOutWriter()

    @Bean
    fun stdErrWriter() = StdErrWriter()

    @Bean
    fun randomNumberGenerator() = RandomNumberGenerator()

}