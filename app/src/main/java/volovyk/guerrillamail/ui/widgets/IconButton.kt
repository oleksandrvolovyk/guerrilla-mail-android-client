package volovyk.guerrillamail.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import volovyk.guerrillamail.R

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String,
    iconBackgroundColor: Color
) {
    Surface(
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.background(color = iconBackgroundColor)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun IconButtonPreview() {
    IconButton(
        painter = painterResource(R.drawable.ic_add),
        contentDescription = "Description",
        iconBackgroundColor = Color.Green
    )
}