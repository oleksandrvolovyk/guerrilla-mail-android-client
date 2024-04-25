package volovyk.guerrillamail.ui.list

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import volovyk.guerrillamail.R
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.ui.theme.GuerrillaMailTheme
import volovyk.guerrillamail.ui.widgets.IconButton
import volovyk.guerrillamail.ui.widgets.verticalScrollbar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmailListScreen(
    uiState: EmailListUiState,
    modifier: Modifier = Modifier,
    onItemClick: (Email) -> Unit = {},
    onItemLongClick: (Email) -> Unit = {},
    onClearSelectionButtonClick: () -> Unit = {},
    onSelectAllButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {}
) {
    Column(modifier) {
        AnimatedVisibility(visible = uiState.selectedEmailsCount > 0) {
            BackHandler { onClearSelectionButtonClick() }

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(32.dp)
                                .clickable { onClearSelectionButtonClick() },
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.clear_selection),
                            iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = uiState.selectedEmailsCount.toString(),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(32.dp)
                            .clickable { onDeleteButtonClick() },
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.clear_selection),
                        iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.selectedEmailsCount == uiState.emails.size,
                        onCheckedChange = { onSelectAllButtonClick() })
                    Text(stringResource(R.string.select_all))
                }
            }
        }

        val lazyColumnState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.verticalScrollbar(state = lazyColumnState, width = 4.dp),
            state = lazyColumnState,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.emails, key = { it.item.id }) { email ->
                EmailListItem(
                    modifier = Modifier.animateItemPlacement(),
                    email,
                    onItemClick,
                    onItemLongClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmailListItem(
    modifier: Modifier = Modifier,
    email: SelectableItem<Email>,
    onItemClick: (Email) -> Unit,
    onItemLongClick: (Email) -> Unit = {}
) {
    val cardContainerColor = if (email.selected) {
        MaterialTheme.colorScheme.surfaceTint
    } else if (email.item.viewed) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35F)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .combinedClickable(
                onClick = { onItemClick(email.item) },
                onLongClick = { onItemLongClick(email.item) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(cardContainerColor.copy(alpha = 1F))
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
                        text = email.item.from,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = email.item.subject,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailListPreview() {
    GuerrillaMailTheme {
        EmailListScreen(
            uiState = EmailListUiState(emails = List(2) {
                SelectableItem(
                    selected = false,
                    Email(
                        id = it.toString(),
                        from = "test$it@example.com",
                        subject = "Subject $it",
                        "Body $it",
                        "Html body $it",
                        "Html body $it",
                        "0$it.0$it.2$it",
                        it % 2 == 0
                    )
                )
            } + List(2) {
                SelectableItem(
                    selected = true,
                    Email(
                        id = (it + 2).toString(),
                        from = "test$it@example.com",
                        subject = "Subject $it",
                        "Body $it",
                        "Html body $it",
                        "Html body $it",
                        "0$it.0$it.2$it",
                        true
                    )
                )
            } + SelectableItem(
                selected = true,
                Email(
                    id = "id",
                    from = "test@example.com",
                    subject = "Fusce at dolor nec magna maximus porttitor. Donec accumsan convallis nisl non vulputate.",
                    "",
                    "",
                    "",
                    "",
                    false
                )
            )
            ))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmailListPreviewDarkTheme() {
    EmailListPreview()
}