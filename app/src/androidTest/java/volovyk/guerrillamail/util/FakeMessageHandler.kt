package volovyk.guerrillamail.util

class FakeMessageHandler : MessageHandler {

    var callsCounter = 0
    var lastDisplayedMessage: String? = null

    override fun showMessage(text: String) {
        callsCounter += 1
        lastDisplayedMessage = text
    }
}