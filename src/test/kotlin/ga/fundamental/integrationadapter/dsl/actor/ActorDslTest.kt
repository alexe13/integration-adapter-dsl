package ga.fundamental.integrationadapter.dsl.actor

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.SimpleActorMapper
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriterActor
import ga.fundamental.integrationadapter.components.source.RequestGenerator
import ga.fundamental.integrationadapter.components.source.RequestGeneratorActor
import ga.fundamental.integrationadapter.dsl.ActorRouter
import ga.fundamental.integrationadapter.dsl.Router
import ga.fundamental.integrationadapter.dsl.destroy
import org.junit.Ignore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.DirectProcessor
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActorDslTest {

    private val gen = RequestGeneratorActor("req-gen-actor", 100_000)
    private val mapper = SimpleActorMapper("actor-mapper") {
        it.copy(payload = (it.payload as? Int)?.times(2) ?: it)
    }
    private val sisOut = StdOutWriterActor("stdOut")
    //
    private val gen2 = RequestGenerator(100_000)
    private val mapper2 = SimpleMapper {
        it.copy(payload = (it.payload as? Int)?.times(2) ?: it)
    }
    private val sisOut2 = StdOutWriter()
    val testBus = DirectProcessor.create<Message>()

    private lateinit var actorRouter: ActorRouter
    private lateinit var router: Router

    @BeforeAll
    fun setUp() {
        actorRouter = ActorRouter {
            pipeline("Test") {
                components {
                    link(gen to mapper)
                    link(mapper to sisOut)
                }
            }
        }
        router = Router {
            pipeline("test") {
                eventBus(testBus)
                scheduler(Schedulers.boundedElastic())
                components {
                    link(gen2 to mapper2)
                    link(mapper2 to sisOut2)
                }
            }
        }
    }

    @AfterAll
    private fun cleanUp() {
        actorRouter.destroy()
        router.destroy()
    }

    @Test
    fun test1() {
        val latch = CountDownLatch(1)
        gen.start()
        latch.await(5000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun test2() {
        val latch = CountDownLatch(1)
        gen2.start()
        latch.await(5000, TimeUnit.MILLISECONDS)
    }

}