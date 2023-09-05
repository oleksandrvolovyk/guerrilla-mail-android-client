package volovyk.guerrillamail.ui

import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import volovyk.guerrillamail.R

object UiHelper {
    fun createConfirmationDialog(
        context: Context,
        message: String,
        onPositiveButtonClick: () -> Unit
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage(message)
        builder.setIcon(R.drawable.ic_launcher_icon)
        builder.setPositiveButton(context.getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            onPositiveButtonClick.invoke()
        }
        builder.setNegativeButton(context.getString(R.string.no)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        return builder.create()
    }

    class AfterTextChangedWatcher(private inline val action: (Editable) -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable) {
            action.invoke(s)
        }
    }
}