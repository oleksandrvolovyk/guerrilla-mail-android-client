package volovyk.guerrillamail.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    iconBackgroundColor: Color
) {
    Surface(
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.background(color = iconBackgroundColor)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun IconButtonPreview() {
    IconButton(
        imageVector = Icons.Default.Add,
        contentDescription = "Description",
        iconBackgroundColor = Color.Green
    )
}