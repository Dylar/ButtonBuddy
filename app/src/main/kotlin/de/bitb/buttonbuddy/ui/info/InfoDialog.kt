package de.bitb.buttonbuddy.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.bitb.buttonbuddy.BuildConfig

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Info") },
        text = { InfoContent() },
        confirmButton = {
            Button(
                onClick = onDismiss,
                content = { Text("OK") }
            )
        }
    )
}

@Composable
private fun InfoContent() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "VersionName: ${BuildConfig.VERSION_NAME}")
        Text(text = "VersionCode: ${BuildConfig.VERSION_CODE}")
        Text(text = "Env: ${BuildConfig.FLAVOR}")
        Text(text = "BuildType: ${BuildConfig.BUILD_TYPE}")
    }
}
