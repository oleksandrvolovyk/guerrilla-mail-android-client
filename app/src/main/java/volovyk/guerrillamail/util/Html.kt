package volovyk.guerrillamail.util

import android.text.Html

object Html {
    fun extractTextFromHtml(html: String): String {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString()
    }
}