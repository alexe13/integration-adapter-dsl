package ga.fundamental.integrationadapter.components

interface Processor<T> {
    fun enqueue(obj: T)
    fun process(obj: T)
    fun next(nextProcessor: Processor<T>)
}