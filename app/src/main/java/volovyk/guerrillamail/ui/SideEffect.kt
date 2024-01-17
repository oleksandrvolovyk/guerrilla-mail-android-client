package volovyk.guerrillamail.ui

interface SideEffect {
    data class ShowToast(val stringId: Int, val stringFormatArg: String? = null) : SideEffect
    data class CopyTextToClipboard(val text: String) : SideEffect
    data class ConfirmAction(
        val messageStringId: Int,
        val stringFormatArg: String? = null,
        val action: () -> Unit
    ) : SideEffect
}