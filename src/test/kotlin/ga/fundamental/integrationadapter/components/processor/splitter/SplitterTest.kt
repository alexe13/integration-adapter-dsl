package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.configure
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SplitterTest {

    @Test
    fun `empty splitter test`() {
        val testBus = ReplayProcessor.create<Message>(1)
        val emptySplitter = Splitter {}
        val splitterDestination = emptySplitter.configure(testBus, "nextComponent")
        //
        val msg1 = Message("1", 1, splitterDestination)
        val msg2 = Message("2", 2, splitterDestination)
        val msg3 = Message("3", 3, splitterDestination)
        //
        StepVerifier.withVirtualTime { testBus }
                .then { testBus.onNext(msg1) }
                .expectNext(msg1)
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg2) }
                .expectNext(msg2)
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg3) }
                .expectNext(msg3)
                .expectNoEvent(Duration.ofSeconds(1))
                .thenCancel().verify(Duration.ofSeconds(3))
    }

    @Test
    fun `split to multiple destinations test`() {
        val testBus = ReplayProcessor.create<Message>(1)
        val component1 = StdOutWriter()
        val destination1 = component1.configure()
        val component2 = StdErrWriter()
        val destination2 = component2.configure()
        val splitter = Splitter {
            -component1
            -component2
        }
        val splitterDestination = splitter.configure(testBus, "nextComponent")
        //
        val msg1 = Message("1", 1, splitterDestination)
        val msg2 = Message("2", 2, splitterDestination)
        val msg3 = Message("3", 3, splitterDestination)
        //
        StepVerifier.withVirtualTime { testBus }
                .then { testBus.onNext(msg1) }
                .expectNext(msg1, msg1.copy(destination = destination1), msg1.copy(destination = destination2))
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg2) }
                .expectNext(msg2, msg2.copy(destination = destination1), msg2.copy(destination = destination2))
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg3) }
                .expectNext(msg3, msg3.copy(destination = destination1), msg3.copy(destination = destination2))
                .expectNoEvent(Duration.ofSeconds(1))
                .thenCancel().verify(Duration.ofSeconds(3))
    }
}