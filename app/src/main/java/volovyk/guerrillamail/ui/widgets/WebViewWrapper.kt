package volovyk.guerrillamail.ui.widgets

import android.graphics.Color
import android.util.AndroidRuntimeException
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import timber.log.Timber
import volovyk.guerrillamail.R

@Composable
fun WebViewWrapper(modifier: Modifier = Modifier, onUpdate: (View) -> Unit) {
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                try {
                    WebView(context).apply {
                        isVerticalScrollBarEnabled = true
                        isHorizontalScrollBarEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                } catch (e: AndroidRuntimeException) {
                    Timber.e("Failed to create WebView: $e")
                    TextView(context).apply {
                        text = context.getString(R.string.failed_to_initialize_webview)
                    }
                }
            },
            update = onUpdate
        )
    }
}