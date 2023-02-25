package de.bitb.buttonbuddy.ui.buddy

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.styles.FireRed
import de.bitb.buttonbuddy.ui.styles.ZergPurple
import de.bitb.buttonbuddy.ui.styles.createComposeView


@AndroidEntryPoint
class BuddyFragment : BaseFragment<BuddyViewModel>() {

    override val viewModel: BuddyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString(KEY_BUDDY_UUID) ?: throw Exception()
        viewModel.initBuddyState(uuid)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        val buddy by viewModel.buddy.observeAsState(null)
        BuddyScreen(buddy)
    }

    @Composable
    fun BuddyScreen(buddy: Buddy?) {
        val info by viewModel.info.observeAsState(null)
        val isMyself = viewModel.uuid == info?.uuid
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopAppBar(title = { Text("Buddy") }) },
            content = {
                when {
                    isMyself && info != null -> InfoDetails(it, info!!)
                    buddy != null -> BuddyDetails(it, buddy)
                    else -> LoadingIndicator()
                }
            },
        )
    }

    @Composable
    fun BuddyDetails(padding: PaddingValues, buddy: Buddy) {
        return DetailScreen(padding, buddy.firstName, buddy.lastName)
    }

    @Composable
    fun InfoDetails(padding: PaddingValues, info: Info) {
        DetailScreen(padding, info.firstName, info.lastName, info.uuid)
    }

    @Composable
    private fun DetailScreen(
        padding: PaddingValues,
        firstName: String,
        lastName: String,
        uuid: String? = null,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Vorname: $firstName")
                Text("Nachname: $lastName")
                if (uuid != null) QrCodeImage(uuid)
            }
        }
    }

    @Composable
    fun QrCodeImage(uuid: String) {
        return AndroidView(
            modifier = Modifier,
            factory = { context ->
                ImageView(context).apply {
                    val qrgEncoder =
                        QRGEncoder(uuid, null, QRGContents.Type.TEXT, 500)
                    qrgEncoder.colorBlack = Color.BLACK//FireRed.value.toInt()
                    qrgEncoder.colorWhite =Color.WHITE// ZergPurple.value.toInt()
                    try {
                        val bitmap = qrgEncoder.bitmap
                        setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        Log.v(toString(), e.toString());
                    }
                }
            }
        )
    }
}
