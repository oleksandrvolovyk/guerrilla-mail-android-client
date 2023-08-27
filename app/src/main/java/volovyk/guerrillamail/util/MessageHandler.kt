package volovyk.guerrillamail.util

import android.content.Context
import android.widget.Toast

interface MessageHandler {
    fun showMessage(text: String)
}

class MessageHandlerImpl(applicationContext: Context) : MessageHandler {

    private val toast = Toast(applicationContext).apply { duration = Toast.LENGTH_SHORT }

    override fun showMessage(text: String) {
        toast.setText(text)
        toast.show()
    }
}