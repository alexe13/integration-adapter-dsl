package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.*
import ga.fundamental.integrationadapter.components.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import reactor.core.publisher.ReplayProcessor

class DslTest {
    private val replayProcessor = ReplayProcessor.create<Message>()

    @Test
    fun test() {

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

        assertThat(Router.pipelines.size).isEqualTo(2)
    }

}