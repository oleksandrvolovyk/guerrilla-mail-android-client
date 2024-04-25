package volovyk.guerrillamail.ui.assigned

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.R
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme
import volovyk.guerrillamail.ui.widgets.EditableEmailRow

@Composable
fun AssignedEmailCard(
    emailUsername: String?,
    emailDomain: String?,
    isGetNewAddressButtonVisible: Boolean,
    modifier: Modifier = Modifier,
    onEmailAddressClick: () -> Unit = {},
    onGetNewAddressButtonClick: () -> Unit = {},
    onEmailUsernameValueChange: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onEmailAddressClick)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (emailUsername == null) {
                    stringResource(R.string.getting_temporary_email)
                } else {
                    stringResource(R.string.your_temporary_email)
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )

            if (emailUsername != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = stringResource(R.string.copy_address_to_clipboard),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

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
                emailDomain = emailDomain ?: "",
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
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
                Text(stringResource(R.string.get_new_address))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NoEmailAssigned_AssignedEmailCardPreview() {
    GuerrillaMailTheme {
        AssignedEmailCard(
            emailUsername = null,
            emailDomain = null,
            isGetNewAddressButtonVisible = false
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun NoEmailAssigned_AssignedEmailCardPreview_DarkTheme() {
    NoEmailAssigned_AssignedEmailCardPreview()
}

@Composable
@Preview(showBackground = true)
fun EmailAssigned_AssignedEmailCardPreview() {
    GuerrillaMailTheme {
        AssignedEmailCard(
            emailUsername = "test",
            emailDomain = "guerrillamail.com",
            isGetNewAddressButtonVisible = false
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun EmailAssigned_AssignedEmailCardPreview_DarkTheme() {
    EmailAssigned_AssignedEmailCardPreview()
}