package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.processor.SimpleMapper
import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.configure
import org.junit.jupiter.api.Test
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.ReplayProcessor
import reactor.test.StepVerifier
import java.time.Duration

class ConditionalSplitterTest {

    @Test
    fun `verify that empty conditional splitter does not broadcast any messages`() {
        val testBus = EmitterProcessor.create<Message>()
        val splitter = ConditionalSplitter {
            //empty!
        }
        val splitterDestination = splitter.configure(testBus)
        //
        val msg1 = Message("1", 1, splitterDestination)
        val msg2 = Message("2", "two", splitterDestination)
        val msg3 = Message("3", 999_999, splitterDestination)
        //
        StepVerifier.withVirtualTime { testBus.log() }
                .expectSubscription()
                .then { testBus.onNext(msg1) }
                .expectNext(msg1)
                .then { testBus.onNext(msg2) }
                .expectNext(msg2)
                .then { testBus.onNext(msg3) }
                .expectNext(msg3)
                .thenCancel().verify(Duration.ofSeconds(3))
    }

    @Test
    fun `verify that splitter with multiple conditions broadcasts messages to all components that match a given predicate`() {
        val testBus = ReplayProcessor.create<Message>(1)
        val mapper = SimpleMapper { it }
        val mapperDestination = mapper.configure(nextDestination = "abc")
        val stdOutWriter = StdOutWriter()
        val stdOutWriterDestination = stdOutWriter.configure()
        val stdErrWriter = StdErrWriter()
        val stdErrWriterDestination = stdErrWriter.configure()
        val splitter = ConditionalSplitter {
            { m: Message -> m.payload is String } > mapper
            { m: Message -> m.payload is Int && m.payload as Int in 0..600 } > stdOutWriter
            { m: Message -> m.payload is Int && m.payload as Int in 500..999_999 } > stdErrWriter
        }
        val splitterDestination = splitter.configure(testBus, "afterMapper")
        //
        val msg1 = Message("1", 299, splitterDestination) //should go to stdOutWriter
        val msg2 = Message("2", "definitely maybe", splitterDestination) //should go to mapper
        val msg3 = Message("3", 7777, splitterDestination) //should go to stdErrWriter
        val msg4 = Message("4", true, splitterDestination) //should go nowhere
        val msg5 = Message("5", 555, splitterDestination) //should go to stdOutWriter and stdErrWriter
        //
        StepVerifier.withVirtualTime { testBus.log() }
                .expectSubscription()
                .then { testBus.onNext(msg1) }
                .expectNext(msg1, msg1.copy(destination = stdOutWriterDestination))
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg2) }
                .expectNext(msg2, msg2.copy(destination = mapperDestination), msg2.copy(destination = "abc"))
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg3) }
                .expectNext(msg3, msg3.copy(destination = stdErrWriterDestination))
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg4) }
                .expectNext(msg4)
                .expectNoEvent(Duration.ofSeconds(1))
                .then { testBus.onNext(msg5) }
                .expectNext(msg5, msg5.copy(destination = stdOutWriterDestination), msg5.copy(destination = stdErrWriterDestination))
                .expectNoEvent(Duration.ofSeconds(1))
                .thenCancel().verify(Duration.ofSeconds(3))
    }

}