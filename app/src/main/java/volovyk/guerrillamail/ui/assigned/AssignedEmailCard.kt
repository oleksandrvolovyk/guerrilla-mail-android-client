package volovyk.guerrillamail.ui.assigned

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.widgets.EditableEmailRow

@Composable
fun AssignedEmailCard(
    emailUsername: String?,
    emailDomain: String?,
    isGetNewAddressButtonVisible: Boolean,
    onEmailAddressClick: () -> Unit = {},
    onGetNewAddressButtonClick: () -> Unit = {},
    onEmailUsernameValueChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = if (emailUsername == null) {
                context.getString(R.string.getting_temporary_email)
            } else {
                context.getString(R.string.your_temporary_email)
            },
            modifier = Modifier
                .clickable(onClick = onEmailAddressClick)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )

        AnimatedVisibility(
            visible = (emailUsername != null && emailDomain != null),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            EditableEmailRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                emailUsername = emailUsername ?: "",
                onEmailUsernameValueChange = onEmailUsernameValueChange,
                emailDomain = emailDomain ?: ""
            )
        }

        AnimatedVisibility(
            visible = isGetNewAddressButtonVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Button(
                onClick = onGetNewAddressButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(context.getString(R.string.get_new_address))
            }
        }
    }
}