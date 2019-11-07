package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExceptionHandlingTest {
    private val replayProcessor = ReplayProcessor.create<Message>(1)
    private val writer = StdOutWriter()
    private val mapper = SimpleMapper {
        val payload = it.payload
        if (payload is Int) {
            if (payload % 2 != 0) {
                return@SimpleMapper it.copy(payload = "$payload and string")
            } else {
                throw java.lang.IllegalArgumentException("Only odd numbers allowed!")
            }
        }
        it
    }

    private lateinit var router: Router

    @BeforeAll
    private fun initRouter() {
        router = Router {
            pipeline("Test") {
                eventBus(replayProcessor)
                components {
                    link(mapper to writer)
                }
            }
        }
    }

    @AfterAll
    private fun cleanUp() {
        router.destroy()
    }

    @Test
    fun `verify that exception in event bus does not kill pipeline`() {
        val mapperDestination = mapper.getOwnDestination()
        val writerDestination = writer.getOwnDestination()

        val msg1 = Message("1", 1, mapperDestination)
        val msg2 = Message("2", 2, mapperDestination) //this will trigger an exception
        val msg3 = Message("3", 3, mapperDestination)
        StepVerifier.withVirtualTime { replayProcessor.log() }
                .then { replayProcessor.onNext(msg1) }
                .expectNext(msg1, msg1.copy(payload = "1 and string", destination = writerDestination))
                .then { replayProcessor.onNext(msg2) }
                .expectNext(msg2) //errored event is handled and do not interrupt the flow
                .then { replayProcessor.onNext(msg3) }
                .expectNext(msg3, msg3.copy(payload = "3 and string", destination = writerDestination))
                .thenCancel()
                .verify()
    }
}