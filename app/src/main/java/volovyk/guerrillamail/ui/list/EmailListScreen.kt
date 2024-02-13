package volovyk.guerrillamail.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme
import volovyk.guerrillamail.ui.widgets.IconButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmailListScreen(
    emails: List<Email>,
    modifier: Modifier = Modifier,
    onItemClick: (Email) -> Unit = {},
    onItemDeleteButtonClick: (Email) -> Unit = {},
    onItemDeleteButtonLongClick: (Email) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(emails, key = { it.id }) { email ->
            EmailListItem(
                modifier = Modifier.animateItemPlacement(),
                email,
                onItemClick,
                onItemDeleteButtonClick,
                onItemDeleteButtonLongClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmailListItem(
    modifier: Modifier = Modifier,
    email: Email,
    onItemClick: (Email) -> Unit,
    onItemDeleteButtonClick: (Email) -> Unit,
    onItemDeleteButtonLongClick: (Email) -> Unit
) {
    val context = LocalContext.current
    val cardContainerColor = if (email.viewed) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35F)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onItemClick(email) },
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = email.from,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = email.subject,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(48.dp)
                        .combinedClickable(
                            onClick = { onItemDeleteButtonClick(email) },
                            onLongClick = { onItemDeleteButtonLongClick(email) }
                        ),
                    imageVector = Icons.Default.Delete,
                    contentDescription = context.getString(R.string.delete),
                    iconBackgroundColor = cardContainerColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailListPreview() {
    GuerrillaMailTheme {
        EmailListScreen(
            emails = List(2) {
                Email(
                    id = it.toString(),
                    from = "test$it@example.com",
                    subject = "Subject $it",
                    "Body $it",
                    "Html body $it",
                    "0$it.0$it.2$it",
                    false
                )
            } + List(2) {
                Email(
                    id = (it + 2).toString(),
                    from = "test$it@example.com",
                    subject = "Subject $it",
                    "Body $it",
                    "Html body $it",
                    "0$it.0$it.2$it",
                    true
                )
            } + Email(
                id = "id",
                from = "test@example.com",
                subject = "Fusce at dolor nec magna maximus porttitor. Donec accumsan convallis nisl non vulputate.",
                "",
                "",
                "",
                false
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmailListPreviewDarkTheme() {
    EmailListPreview()
}