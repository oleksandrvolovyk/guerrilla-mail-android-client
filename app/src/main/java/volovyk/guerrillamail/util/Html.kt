package volovyk.guerrillamail.util

import android.text.Html

interface HtmlTextExtractor {
    fun extractText(html: String): String
}

class AndroidHtmlTextExtractor : HtmlTextExtractor {
    override fun extractText(html: String): String {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString()
    }
}