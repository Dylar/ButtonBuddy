package de.bitb.buttonbuddy.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceComposable(
    item: PreferenceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(all = 16.dp),
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.body1,
            color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else Color.Unspecified,
        )

        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.body2,
            color = if (!enabled) MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled) else MaterialTheme.colors.onSurface.copy(
                alpha = ContentAlpha.medium
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegularPreferencePreview() {
    PreferenceComposable(
        item = PreferenceItem("Advanced settings", "Lorem ipsum dolor sit amet"),
        onClick = { },
    )
}