package volovyk.guerrillamail.ui.specific

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewWrapper(modifier: Modifier = Modifier, onUpdate: (WebView) -> Unit) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
            }
        },
        update = onUpdate
    )
}