package volovyk.guerrillamail.data.emails.remote.guerrillamail

import java.net.URLDecoder

object GuerrillaMailBodyUnfilter {
    private val pattern1 = Regex("""/res\.php\?r=1&amp;n=[a-z]+&amp;q=([^"^&]+)""")
    private val pattern2 = Regex("""&quot;/res\.php\?r=1&amp;n=[a-z]+&amp;q=([^"]+)&quot;""")

    operator fun invoke(filteredHtmlBody: String): String {
        var unfilteredHtml = pattern1.replace(filteredHtmlBody) { result ->
            val encodedUrl = result.groupValues[1]
            unescape(encodedUrl)
        }

        unfilteredHtml = pattern2.replace(unfilteredHtml) { result ->
            val encodedUrl = result.groupValues[1]
            "&quot;${unescape(encodedUrl)}&quot;"
        }

        return unfilteredHtml
    }

    private fun unescape(url: String): String {
        return URLDecoder.decode(url, "UTF-8")
    }
}