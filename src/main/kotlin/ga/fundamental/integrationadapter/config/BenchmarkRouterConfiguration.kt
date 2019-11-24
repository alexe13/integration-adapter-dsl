package ga.fundamental.integrationadapter.config

import ga.fundamental.integrationadapter.components.processor.HttpDelayProcessor
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RequestGenerator
import ga.fundamental.integrationadapter.dsl.Router
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Duration

@Profile("benchmark")
@Configuration
class BenchmarkRouterConfiguration {

    @Bean
    fun router(): Router {
        return Router {
            pipeline("Request benchmark") {
                components {
                    link(requestGenerator() to httpDelayProcessor())
                    link(httpDelayProcessor() to stdOutWriter())
                }
            }
        }
    }

    @Bean
    fun requestGenerator() = RequestGenerator(100)

    @Bean
    fun httpDelayProcessor() = HttpDelayProcessor(2)

    @Bean
    fun stdOutWriter() = StdOutWriter()

}