package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Simulates heavy network interactions
 */
class HttpDelayProcessor(delaySeconds: Int = 1) : AbstractAsyncMessageProcessor() {
    private val url = "https://postman-echo.com/delay/$delaySeconds"
    private val httpClient = WebClient.create(url)

    override fun processAsync(message: Message) =
            httpClient.get().retrieve().bodyToMono<String>()
                    .map { Message(System.currentTimeMillis().toString(), it) }
                    .log()

    override fun getOwnDestination() = "HttpDelayProcessor${hashCode()}"
}