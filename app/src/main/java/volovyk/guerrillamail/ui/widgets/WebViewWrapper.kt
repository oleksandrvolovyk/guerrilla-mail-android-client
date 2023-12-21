package volovyk.guerrillamail.ui.widgets

import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewWrapper(modifier: Modifier = Modifier, onUpdate: (WebView) -> Unit) {
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    isVerticalScrollBarEnabled = true
                    isHorizontalScrollBarEnabled = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    setBackgroundColor(Color.TRANSPARENT)
                }
            },
            update = onUpdate
        )
    }
}