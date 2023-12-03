package volovyk.guerrillamail.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import volovyk.guerrillamail.data.emails.model.Email

@Composable
fun EmailList(
    emails: List<Email>,
    onItemClick: (Email) -> Unit = {},
    onItemDeleteButtonClick: (Email) -> Unit = {},
    onItemDeleteButtonLongClick: (Email) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
    ) {
        items(emails) { email ->
            EmailListItem(
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
    email: Email,
    onItemClick: (Email) -> Unit,
    onItemDeleteButtonClick: (Email) -> Unit,
    onItemDeleteButtonLongClick: (Email) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 8.dp)
            .clickable { onItemClick(email) }
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
                horizontalArrangement = Arrangement.SpaceBetween
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
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .combinedClickable(
                            onClick = { onItemDeleteButtonClick(email) },
                            onLongClick = { onItemDeleteButtonLongClick(email) }
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailListPreview() {
    EmailList(
        emails = List(3) {
            Email(
                id = "it",
                from = "test$it@example.com",
                subject = "Subject $it",
                "Body $it",
                "Html body $it",
                "0$it.0$it.2$it",
                false
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