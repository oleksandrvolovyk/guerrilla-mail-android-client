package volovyk.guerrillamail.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import timber.log.Timber
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.UiHelper.createConfirmationDialog
import volovyk.guerrillamail.ui.UiHelper.showToast

sealed interface SideEffect {
    data class ShowToast(
        @StringRes val stringId: Int,
        val stringFormatArg: String? = null
    ) : SideEffect

    data class CopyTextToClipboard(
        val text: String
    ) : SideEffect

    data class ConfirmAction(
        @StringRes val messageStringId: Int,
        val stringFormatArg: String? = null,
        val action: () -> Unit
    ) : SideEffect
}

fun handleSideEffect(context: Context, sideEffect: SideEffect) = when (sideEffect) {
    is SideEffect.CopyTextToClipboard -> {
        try {
            val clipboard =
                context.getSystemService(ComponentActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText(context.getString(R.string.app_name), sideEffect.text)
            clipboard.setPrimaryClip(clip)
        } catch (e: SecurityException) {
            Timber.e("Clipboard write caused an exception: $e")
        }
    }

    is SideEffect.ShowToast -> {
        context.showToast(context.getString(sideEffect.stringId))
    }

    is SideEffect.ConfirmAction -> {
        createConfirmationDialog(
            context,
            context.getString(sideEffect.messageStringId, sideEffect.stringFormatArg)
        ) {
            sideEffect.action()
        }.show()
    }
}