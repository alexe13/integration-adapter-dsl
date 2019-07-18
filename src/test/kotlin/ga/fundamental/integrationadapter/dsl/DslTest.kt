package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message
import ga.fundamental.integrationadapter.components.TestProcessor1
import ga.fundamental.integrationadapter.dsl.Router.router
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DslTest {

    val processor1 = TestProcessor1<Message>()
    val processor2 = TestProcessor1<Message>()
    val processor3 = TestProcessor1<Message>()
    val processor4 = TestProcessor1<Message>()

    @Test
    fun test() {

        router {
            pipeline("CB courses") {
                link(processor1 to processor2)
                link(processor2 to processor3)
                link(processor3 to processor4)
            }
            pipeline("RFQ request") {
                link(processor4 to processor2)
            }
        }

        assertThat(Router.pipelines.size).isEqualTo(2)
    }

}