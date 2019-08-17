package ga.fundamental.integrationadapter.dsl

import ga.fundamental.integrationadapter.components.Message

infix fun <T> Link<T>.`on condition that`(condition: (Message) -> Boolean): Link<T> {
    return this.apply { predicate = condition }
}

