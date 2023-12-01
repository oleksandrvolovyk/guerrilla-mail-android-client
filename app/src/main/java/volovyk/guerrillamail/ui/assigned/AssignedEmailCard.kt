package volovyk.guerrillamail.ui.assigned

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.R

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
    Column(
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

        if (emailUsername != null && emailDomain != null) {
            EditableEmailRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                emailUsername = emailUsername,
                onEmailUsernameValueChange = onEmailUsernameValueChange,
                emailDomain = emailDomain
            )
        }

        if (isGetNewAddressButtonVisible) {
            Button(
                onClick = onGetNewAddressButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Get New Address")
            }
        }
    }
}