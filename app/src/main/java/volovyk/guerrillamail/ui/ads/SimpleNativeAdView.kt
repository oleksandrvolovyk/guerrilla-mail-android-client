package volovyk.guerrillamail.ui.ads

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import volovyk.guerrillamail.R

@Composable
fun SimpleNativeAdView(nativeAd: NativeAd, modifier: Modifier = Modifier) {
    // Extract current theme colors
    val currentTheme = MaterialTheme.colorScheme

    val templateStyle = remember {
        NativeTemplateStyle.Builder()
            .withMainBackgroundColor(
                ColorDrawable(
                    currentTheme.surfaceVariant.copy(alpha = 0.7f).toArgb()
                )
            )
            .withCallToActionBackgroundColor(ColorDrawable(currentTheme.primary.toArgb()))
            .withCallToActionTypefaceColor(currentTheme.onPrimary.toArgb())
            .withCallToActionTextSize(14f)
            .withPrimaryTextTypefaceColor(currentTheme.onSurface.toArgb())
            .withPrimaryTextSize(16f)
            .withSecondaryTextTypefaceColor(currentTheme.onSurfaceVariant.toArgb())
            .withSecondaryTextSize(14f)
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            // Inflate the XML layout for the NativeAdView
            val inflater = LayoutInflater.from(context)
            val adView = inflater.inflate(R.layout.native_ad_layout_v2, null) as TemplateView
            adView.setStyles(templateStyle)
            adView.setNativeAd(nativeAd)
            adView
        },
        update = { adView ->
            // Update colors based on current theme
            adView.setStyles(templateStyle)
        }
    )
}