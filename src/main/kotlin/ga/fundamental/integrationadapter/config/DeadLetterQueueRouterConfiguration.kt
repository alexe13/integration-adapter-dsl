package ga.fundamental.integrationadapter.config

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.ErrorAccumulator
import ga.fundamental.integrationadapter.components.processor.splitter.ConditionalSplitter
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.ReplayProcessor
import java.time.Duration

@Profile("deadLetterQueue")
@Configuration
class DeadLetterQueueRouterConfiguration {

    private val eventBus = ReplayProcessor.create<Message>(1)

    @Bean
    fun router(): Router {
        return Router {
            pipeline("Generate random numbers") {
                eventBus(eventBus)
                components {
                    link(randomNumberGenerator() to randomNumberSplitter())
                }
            }
            pipeline("Report deviations") {
                eventBus(eventBus)
                components {
                    link(errorAccumulator() to stdErrWriter())
                }
            }

        }
    }

    @Bean
    fun randomNumberGenerator() = RandomNumberGenerator(Duration.ofSeconds(5))

    @Bean
    fun randomNumberSplitter() = ConditionalSplitter {
        { m : Message -> m.payload is Double && m.payload <= 100 } > stdOutWriter();
        { m : Message -> m.payload is Double && m.payload > 100 } > errorAccumulator()
    }

    @Bean
    fun stdOutWriter() = StdOutWriter()

    @Bean
    fun stdErrWriter() = StdErrWriter()

    @Bean
    fun errorAccumulator() = ErrorAccumulator()

}