package volovyk.guerrillamail.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditableEmailRow(
    modifier: Modifier = Modifier,
    emailUsername: String,
    onEmailUsernameValueChange: (String) -> Unit = {},
    emailDomain: String
) {
    Row(
        modifier = modifier
    ) {
        BasicTextField(
            value = emailUsername,
            onValueChange = onEmailUsernameValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            textStyle = TextStyle.Default.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )

        Text(text = "@$emailDomain")
    }
}

@Composable
@Preview(showBackground = true)
fun EditableEmailRow1() {
    EditableEmailRow(emailUsername = "test", emailDomain = "example.com")
}