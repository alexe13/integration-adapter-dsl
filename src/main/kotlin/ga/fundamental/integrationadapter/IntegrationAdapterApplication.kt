package ga.fundamental.integrationadapter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.concurrent.CountDownLatch

@SpringBootApplication
class IntegrationAdapterApplication


fun main(args: Array<String>) {
    runApplication<IntegrationAdapterApplication>(*args)

    val countDownLatch = CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(Thread {countDownLatch.countDown()})
    countDownLatch.await()
}
