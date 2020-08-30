package ga.fundamental.integrationadapter

import org.openjdk.jmh.annotations.Benchmark

open class JmhTest {

    @Benchmark
    fun sample() {
        val a = 1
        val b = 2
        val sum = a + b
    }
}