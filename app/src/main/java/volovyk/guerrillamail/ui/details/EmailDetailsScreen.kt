package volovyk.guerrillamail.ui.details

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme
import volovyk.guerrillamail.ui.widgets.WebViewWrapper

@Composable
fun EmailDetailsScreen(
    uiState: EmailDetailsUiState,
    modifier: Modifier = Modifier,
    onHtmlRenderSwitchCheckedChange: (Boolean) -> Unit = {},
    onFromFieldClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.clickable(onClick = onFromFieldClick),
                    text = context.getString(R.string.from, uiState.email?.from),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = context.getString(R.string.date, uiState.email?.date),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = context.getString(R.string.subject, uiState.email?.subject),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.Start)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.testTag(context.getString(R.string.test_tag_html_render_switch)),
                checked = uiState.renderHtml,
                onCheckedChange = onHtmlRenderSwitchCheckedChange
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = context.getString(R.string.toggle_html_rendering),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (uiState.renderHtml) {
            WebViewWrapper(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .testTag(context.getString(R.string.test_tag_email_body_web_view)),
                onUpdate = {
                    it.loadData(uiState.email?.htmlBody ?: "", "text/html", "base64")
                }
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                SelectionContainer {
                    Text(
                        text = uiState.email?.body ?: "",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = Int.MAX_VALUE
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EmailDetailsScreenPreview() {
    GuerrillaMailTheme {
        EmailDetailsScreen(
            uiState = EmailDetailsUiState(
                email = Email(
                    id = "1",
                    from = "from@example.com",
                    subject = "Test subject",
                    body = "Test body",
                    htmlBody = "Test html body",
                    date = "Today",
                    viewed = false
                ),
                renderHtml = false
            )
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun EmailDetailsScreenPreviewDarkTheme() {
    EmailDetailsScreenPreview()
}