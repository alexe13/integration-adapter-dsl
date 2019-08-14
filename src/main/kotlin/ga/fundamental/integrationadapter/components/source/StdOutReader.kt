package ga.fundamental.integrationadapter.components.source

import ga.fundamental.integrationadapter.components.Message

class StdOutReader : AbstractMessageSupplier() {
    override fun getOwnDestination() = "StdOutReader#${hashCode()}"

    init {
        Thread {
            while (true) {
                readLine()?.let {
                    publishEvent(Message(System.currentTimeMillis().toString(), it))
                }
            }
        }.start()
    }
}