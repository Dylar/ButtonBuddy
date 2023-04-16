package de.bitb.buttonbuddy.ui.profile

import android.util.Log
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.base.naviToSettings

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {
    companion object {
        const val APPBAR_TAG = "ProfileAppbar"
        const val SETTINGS_BUTTON_TAG = "ProfileSettingsButton"
        const val QR_INFO_TAG = "ProfileQRInfo"
        const val QR_TAG = "ProfileQR"
    }

    override val viewModel: ProfileViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val user by viewModel.user.observeAsState(null)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.profile_title)) },
                    actions = {
                        IconButton(
                            modifier = Modifier.testTag(SETTINGS_BUTTON_TAG),
                            onClick = ::naviToSettings
                        ) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                    },
                )
            },
            content = {
                when {
                    user != null -> UserDetails(it, user!!)
                    else -> LoadingIndicator()
                }
            },
        )
    }

    @Composable
    fun UserDetails(padding: PaddingValues, user: User) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag(QR_INFO_TAG),
                    contentAlignment = Alignment.Center,
                ) { Text(getString(R.string.profile_qr_info)) }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) { QrCodeImage(user.uuid) }
            }
        }
    }

    @Composable
    fun QrCodeImage(uuid: String) {
        val black = MaterialTheme.colors.background
        val white = MaterialTheme.colors.onBackground
        return AndroidView(
            modifier = Modifier.testTag(QR_TAG),
            factory = { context ->
                ImageView(context).apply {
                    QRGEncoder(uuid, null, QRGContents.Type.TEXT, 800).apply {
                        colorBlack = black.toArgb()
                        colorWhite = white.toArgb()
                        try {
                            setImageBitmap(bitmap)
                        } catch (e: WriterException) {
                            Log.e(toString(), e.toString())
                        }
                    }
                }
            }
        )
    }

}
