package de.bitb.buttonbuddy.ui.base.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.permission.PermissionDialogTags.PERMISSION_DIALOG

object PermissionDialogTags {
    const val PERMISSION_DIALOG = "PermissionDialog"
}

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(
                    text = buttonText(isPermanentlyDeclined).asString(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (isPermanentlyDeclined) onGoToAppSettingsClick() else onOkClick() }
                        .padding(16.dp)
                )
            }
        },
        title = { Text(text = stringResource(R.string.permission_needed)) },
        text = {
            Text(
                text = permissionTextProvider
                    .getDescription(isPermanentlyDeclined = isPermanentlyDeclined)
                    .asString()
            )
        },
        modifier = modifier.testTag(PERMISSION_DIALOG)
    )
}

private fun buttonText(isPermanentlyDeclined: Boolean): ResString = ResString.ResourceString(
    if (isPermanentlyDeclined) R.string.grant_permission
    else R.string.ok
)

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): ResString
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): ResString =
        ResString.ResourceString(
            if (isPermanentlyDeclined) R.string.camera_permission_declined
            else R.string.camera_permission_asking
        )
}