package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.LetterCounter
import ga.fundamental.integrationadapter.components.SimpleMapper
import ga.fundamental.integrationadapter.dsl.Router.router
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DslTest {

    val processor1 = SimpleMapper()
    val processor2 = LetterCounter()
    val processor3 = SimpleMapper()
    val processor4 = LetterCounter()

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