package ga.fundamental.integrationadapter.components

interface Processor<T> : ReactiveComponent<T> {
    fun process(obj: T)
}