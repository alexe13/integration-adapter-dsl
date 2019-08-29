package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.processor.splitter.ConditionalSplitter
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.components.source.StdOutReader
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import reactor.core.publisher.ReplayProcessor

class DslTest {
    private val replayProcessor = ReplayProcessor.create<Message>()
    private val reader1 = StdOutReader()
    private val mapper1 = SimpleMapper { it }
    private val writer1 = StdOutWriter()
    private val numberGenerator = RandomNumberGenerator()
    private val splitter = ConditionalSplitter {
        when {
            it.payload is Number -> okWriter
            else -> errWriter
        }
    }
    private val okWriter = StdOutWriter()
    private val errWriter = StdErrWriter()

    private val router =
            Router {
                pipeline("Pipeline1") {
                    eventBus(replayProcessor)
                    components {
                        link(reader1 to mapper1)
                        link(mapper1 to writer1)
                    }
                }
                pipeline("Pipeline2") {
                    components {
                        link(numberGenerator to splitter)
                    }
                }
            }


    @Test
    fun `verify basic structure`() {
        assertThat(router).isNotNull
        assertThat(router.pipelines).hasSize(2)
        assertThat(router.pipelines.keys).contains("Pipeline1")
        assertThat(router.pipelines.keys).contains("Pipeline2")
    }
}