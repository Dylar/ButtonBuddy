package de.bitb.buttonbuddy.ui.buddy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.base.styles.BabyBlue
import de.bitb.buttonbuddy.ui.base.styles.ZergPurple
import de.bitb.buttonbuddy.ui.base.styles.createComposeView


@AndroidEntryPoint
class BuddyFragment : BaseFragment<BuddyViewModel>() {
    companion object {
        const val APPBAR_TAG = "BuddyAppbar"
        const val SEND_BUTTON = "BuddySendButton"
    }

    override val viewModel: BuddyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString(KEY_BUDDY_UUID) ?: throw Exception()
        viewModel.initLiveState(uuid)
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
        val title = if (isMyself) "Profil" else "Buddy${buddy?.let { ": " + it.fullName } ?: ""}"
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(title) }
                )
            },
            floatingActionButton = {
                if (buddy != null && !isMyself) {
                    FloatingActionButton(
                        modifier = Modifier.testTag(SEND_BUTTON),
                        onClick = { viewModel.sendMessage(buddy) }
                    ) { Icon(Icons.Filled.Send, contentDescription = "Send") }
                }
            },
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
    fun InfoDetails(padding: PaddingValues, info: Info) {
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
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Mit diesem QR-Code können Buddys Sie hinzufügen") }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) { QrCodeImage(info.uuid) }
            }
        }
    }

    @Composable
    fun QrCodeImage(uuid: String) {
        val black = MaterialTheme.colors.background
        val white = MaterialTheme.colors.onBackground
        return AndroidView(
            modifier = Modifier,
            factory = { context ->
                ImageView(context).apply {
                    QRGEncoder(uuid, null, QRGContents.Type.TEXT, 800).apply {
                        colorBlack = black.toArgb()
                        colorWhite = white.toArgb()
                        try {
                            setImageBitmap(bitmap)
                        } catch (e: WriterException) {
                            Log.v(toString(), e.toString());
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun BuddyDetails(padding: PaddingValues, buddy: Buddy) {
        val messages by viewModel.messages.observeAsState(null)
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { MessagesList(innerPadding = padding, messages = messages, buddy.uuid) }
    }

    @Composable
    fun MessagesList(innerPadding: PaddingValues, messages: List<Message>?, uuid: String) {
        when {
            messages == null -> LoadingIndicator()
            messages.isEmpty() ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(text = "Ihr habt noch nicht an euch gedacht")
                }
            else -> {
                LazyColumn(contentPadding = innerPadding) {
                    items(messages.size) { MessageListItem(messages[it], uuid) }
                }
            }
        }
    }

    @Composable
    fun MessageListItem(msg: Message, uuid: String) {
        val isMyMessage = uuid == msg.fromUuid
        val backgroundColor =
            if (isMyMessage) MaterialTheme.colors.primary else MaterialTheme.colors.surface
        val textColor =
            if (isMyMessage) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isMyMessage) Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = 4.dp,
                backgroundColor = backgroundColor
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = msg.title, color = textColor, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(text = msg.formatDate, color = textColor, fontSize = 12.sp)
                    }
                    Text(text = msg.message, color = textColor, fontSize = 16.sp)
                }
            }
            if (isMyMessage) Spacer(modifier = Modifier.weight(1f))
        }
    }

}
