package ga.fundamental.integrationadapter.components

class TestProcessor1<T> : AbstractQueueProcessor<T>() {
    override fun processInternal(obj: T): T {
        println("Test processor 1. Processing $obj")
        return obj
    }

}