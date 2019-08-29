package ga.fundamental.integrationadapter.components.processor

import ga.fundamental.integrationadapter.components.Message
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * Simulates heavy network interactions
 */
class HttpDelayProcessor(delaySeconds: Int = 1) : AbstractMessageProcessor() {

    private val url = "https://postman-echo.com/delay/$delaySeconds"
    private val httpClient = WebClient.create(url)

    override fun processInternal(message: Message): Message {
        httpClient.get().retrieve().bodyToMono<String>().subscribe(::println, ::println)
        return message
    }

    override fun getOwnDestination() = "HttpDelayProcessor${hashCode()}"
}