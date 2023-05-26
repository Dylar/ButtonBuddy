package de.bitb.buttonbuddy.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bitb.buttonbuddy.R

data class MenuItem(
    val tag:String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onTap: () -> Unit,
)

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) { Text(text = stringResource(R.string.app_name), fontSize = 48.sp) }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
//                    .fillMaxWidth()
                    .clickable { item.onTap() }
                    .padding(16.dp)
                    .testTag(item.tag)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}