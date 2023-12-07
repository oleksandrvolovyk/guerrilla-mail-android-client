package volovyk.guerrillamail.ui

import android.content.Context
import android.content.DialogInterface
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
}