package ga.fundamental.integrationadapter.components

data class Message(val id: String, val payload: Any) {
    var destination: String = ""
}