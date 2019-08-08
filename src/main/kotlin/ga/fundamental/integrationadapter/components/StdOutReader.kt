package ga.fundamental.integrationadapter.components

class StdOutReader : AbstractMessageSupplier("stdOutReader") {

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