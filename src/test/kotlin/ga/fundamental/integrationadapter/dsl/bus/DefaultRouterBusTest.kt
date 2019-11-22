package ga.fundamental.integrationadapter.dsl.bus

import ga.fundamental.integrationadapter.components.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DefaultRouterBusTest {

    @Test
    fun `publish to a single subscriber`() {
        val bus = DefaultRouterBus()
        val msg1 = Message("1", 1)
        //
        StepVerifier.create(bus.get().log())
                .then { bus.publish(msg1) }
                .expectNext(msg1)
                .thenCancel().verify(Duration.ofSeconds(3))
    }

    @Test
    fun `verify that exceptions in upstream can be caught and flux does not terminate`() {
        val counter = AtomicInteger(2)
        val bus = DefaultRouterBus()
        val msg1 = Message("1", 1)
        val msg2 = Message("2", 2)
        val msg3 = Message("3", 3)
        val errFlux = bus.get().log().doOnNext { if (it.id == "2") throw java.lang.RuntimeException("Surprise!") }
        val sub1 = errFlux.onErrorContinue { _, _ -> }.subscribe({
            counter.decrementAndGet()
        }) { System.err.println("On error: $it") }
        //
        StepVerifier.create(errFlux)
                .then { bus.publish(msg1) }
                .expectNext(msg1)
                .then { bus.publish(msg2) }
                .then { bus.publish(msg3) }
                .expectErrorMatches { it.message!!.contains("Surprise!") }
                .verify(Duration.ofSeconds(3))
        sub1.dispose()
        assertThat(counter.get()).isEqualTo(0) //verify that subscriber received messages 1 and 3
    }

    @Test
    fun `slow subscribers, fast producer - thread pool`() {
        val latch = CountDownLatch(1)
        val collection = CopyOnWriteArrayList<String>()
        val bus = DefaultRouterBus(Schedulers.boundedElastic())
        val busFlux = bus.get().log()
        val slow1 = busFlux.subscribe {
            collection.add("Slow1")
            Thread.sleep(50)
            if (it.id == "10") latch.countDown() //received last element
        }
        val slow2 = busFlux.subscribe {
            collection.add("Slow2")
            Thread.sleep(25)
        }

        for (i in 1..10) {
            bus.publish(Message(i.toString(), i))
            Thread.sleep(10)
        }

        latch.await(3, TimeUnit.SECONDS)
        //subscribers process elements each in its own thread
        assertThat(collection.take(10)).contains("Slow1", "Slow2")
        assertThat(collection.takeLast(10)).contains("Slow1", "Slow2")
        slow1.dispose()
        slow2.dispose()
    }
}