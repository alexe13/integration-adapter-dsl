package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.processor.splitter.ConditionalSplitter
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.components.source.StdOutReader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.ReplayProcessor
import reactor.core.scheduler.Schedulers
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DslTest {
    private val replayProcessor = ReplayProcessor.create<Message>()
    private val reader1 = StdOutReader()
    private val mapper1 = SimpleMapper { it }
    private val writer1 = StdOutWriter()
    private val numberGenerator = RandomNumberGenerator(Duration.ofSeconds(5))
    private val okWriter = StdOutWriter()
    private val errWriter = StdErrWriter()
    private val splitter = ConditionalSplitter {
        { m: Message -> m.payload is Number } > okWriter
        { m: Message -> m.payload !is Number } > errWriter
    }

    private lateinit var router: Router

    @BeforeAll
    private fun initRouter() {
        router = Router {
            pipeline("Pipeline1") {
                eventBus(replayProcessor)
                components {
                    link(reader1 to mapper1)
                    link(mapper1 to writer1)
                }
            }
            pipeline("Pipeline2") {
                scheduler(Schedulers.boundedElastic())
                components {
                    link(numberGenerator to splitter)
                }
            }
        }
    }

    @AfterAll
    private fun cleanUp() {
        router.destroy()
    }

    @Test
    fun `verify basic structure`() {
        assertThat(router).isNotNull
        assertThat(router.pipelines).hasSize(2)
        assertThat(router.pipelines.keys).contains("Pipeline1")
        assertThat(router.pipelines.keys).contains("Pipeline2")
    }

    @Test
    fun `verify first pipeline`() {
        val pipeline1 = router.pipelines["Pipeline1"]
        assertThat(pipeline1).extracting { it?.eventBus }.isInstanceOf(ReplayProcessor::class.java)
        assertThat(pipeline1).extracting { it?.eventBus }.isSameAs(replayProcessor)
        assertThat(pipeline1).extracting { it?.scheduler }.isSameAs(Schedulers.single())
        val components1 = pipeline1?.components
        assertThat(components1).hasSize(2)
        assertThat(components1?.flatMap { it.pair.toList() }).containsAll(listOf(reader1, mapper1, writer1))
    }

    @Test
    fun `verify second pipeline`() {
        val pipeline2 = router.pipelines["Pipeline2"]
        assertThat(pipeline2).extracting { it?.eventBus }.isInstanceOf(ReplayProcessor::class.java)
        assertThat(pipeline2).extracting { it?.eventBus }.isNotSameAs(replayProcessor)
        assertThat(pipeline2).extracting { it?.scheduler }.isSameAs(Schedulers.boundedElastic())
        val components2 = pipeline2?.components
        assertThat(components2).hasSize(1)
        assertThat(components2?.flatMap { it.pair.toList() }).containsAll(listOf(numberGenerator, splitter))
    }
}