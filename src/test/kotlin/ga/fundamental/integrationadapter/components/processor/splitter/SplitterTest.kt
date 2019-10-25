package ga.fundamental.integrationadapter.components.processor.splitter

import ga.fundamental.integrationadapter.components.sink.StdErrWriter
import ga.fundamental.integrationadapter.components.sink.StdOutWriter
import ga.fundamental.integrationadapter.components.source.RandomNumberGenerator
import ga.fundamental.integrationadapter.dsl.Router
import ga.fundamental.integrationadapter.dsl.destroy
import org.junit.jupiter.api.*
import java.time.Duration
import java.util.concurrent.CountDownLatch

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SplitterTest {

    private val randomNumberGenerator = RandomNumberGenerator(Duration.ofSeconds(1))
    private val stdOutWriter = StdOutWriter()
    private val stdOutWriter2 = StdOutWriter()
    private val stdErrWriter = StdErrWriter()
    private val splitter = Splitter {
        -stdOutWriter
        -stdOutWriter2
        -stdErrWriter
    }

    private lateinit var router: Router

    @BeforeAll
    private fun initRouter() {
        router = Router {
            pipeline("A") {
                components {
                    link(randomNumberGenerator to splitter)
                }
            }
        }
    }

    @AfterAll
    private fun cleanUp() {
        router.destroy()
    }


    @Test
    @Disabled
    fun test() {
        val latch = CountDownLatch(1)
        latch.await()
    }
}