package ga.fundamental.integrationadapter.components

import java.util.concurrent.LinkedBlockingQueue

abstract class AbstractQueueProcessor<T> : Processor<T> {
    private val queue = LinkedBlockingQueue<T>()

    private var nextProcessor: Processor<T>? = null

    override fun process(obj: T) {
        nextProcessor?.enqueue(processInternal(queue.take()))
    }

    override fun next(nextProcessor: Processor<T>) {
        this.nextProcessor = nextProcessor
    }

    override fun enqueue(obj: T) {
        queue.add(obj)
    }

    abstract fun processInternal(obj: T) : T
}