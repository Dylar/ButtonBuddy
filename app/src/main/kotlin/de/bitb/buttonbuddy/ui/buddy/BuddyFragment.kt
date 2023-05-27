package de.bitb.buttonbuddy.ui.buddy

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.KEY_BUDDY_UUID
import de.bitb.buttonbuddy.core.misc.*
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.CoolDownButton
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import java.util.*


@AndroidEntryPoint
class BuddyFragment : BaseFragment<BuddyViewModel>() {
    companion object {
        const val APPBAR_TAG = "BuddyAppbar"
        const val SEND_BUTTON_TAG = "BuddySendButton"
        const val LIST_TAG = "BuddyList"

        const val COOLDOWN_SENDING_TAG = "BuddyCooldownSending"
        const val COOLDOWN_RECEIVING_TAG = "BuddyCooldownReceiving"
    }

    override val viewModel: BuddyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getString(KEY_BUDDY_UUID) ?: throw Exception()
        viewModel.initLiveState(uuid)
    }

    @Composable
    override fun ScreenContent() {
        val buddy by viewModel.buddy.observeAsState(null)
        val messages by viewModel.messages.observeAsState(null)
        val settings by viewModel.settingsRepo.getLiveSettings().observeAsState()
        val title = getString(R.string.buddy_title, buddy?.let { ": ${it.fullName}" } ?: "")
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(title) },
                )
            },
            floatingActionButton = {
                if (buddy != null) {
                    val lastMsgDate = messages?.lastOrNull()?.date ?: Date(0)
                    val cooldown =
                        buddy?.uuid.let { settings?.buddysCooldown?.get(it) } ?: DEFAULT_COOLDOWN
                    CoolDownButton(lastMsgDate, cooldown)
                    {
                        FloatingActionButton(
                            modifier = Modifier.testTag(SEND_BUTTON_TAG),
                            onClick = { viewModel.sendMessageToBuddy(buddy as Buddy) }
                        ) { Icon(Icons.Filled.Send, contentDescription = "Send") }
                    }
                }
            },
            content = {
                when {
                    settings != null && buddy != null -> BuddyDetails(
                        it,
                        settings as Settings,
                        buddy as Buddy,
                        messages
                    )
                    else -> LoadingIndicator()
                }
            },
        )
    }

    @Composable
    fun BuddyDetails(
        padding: PaddingValues,
        settings: Settings,
        buddy: Buddy,
        messages: List<Message>?
    ) {
        val showDialog = remember { mutableStateOf(false) }
        val timePicked = remember { mutableStateOf(buddy.cooldown) }
        val cooldown = settings.buddysCooldown[buddy.uuid] ?: 0
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Text(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ).testTag(COOLDOWN_SENDING_TAG),
                text = if (cooldown != 0L) getString(
                    R.string.cooldown_sending,
                    formatDuration(cooldown)
                )
                else getString(R.string.cooldown_sending_always),
                textAlign = TextAlign.Center,
            )
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .testTag(COOLDOWN_RECEIVING_TAG),
                onClick = { showDialog.value = true },
                content = {
                    Text(
                        text = if (timePicked.value != 0L) getString(
                            R.string.cooldown_receiving,
                            formatDuration(timePicked.value)
                        )
                        else getString(R.string.cooldown_receiving_always),
                        textAlign = TextAlign.Center,
                    )
                },
            )
            Divider()
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            ) { MessagesList(innerPadding = padding, messages = messages, buddy.uuid) }
        }

        if (showDialog.value) {
            val value = timePicked.value
            TimerPicker(
                hour = getHours(value),
                min = getMins(value),
                onCancel = { showDialog.value = false },
                onSelected = { h, m ->
                    timePicked.value = calculateMilliseconds(h, m)
                    showDialog.value = false
                    viewModel.setCooldown(buddy, h, m)
                },
            )
        }
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
                ) { Text(text = getString(R.string.buddy_no_messages)) }
            else -> {
                LazyColumn(modifier = Modifier.testTag(LIST_TAG), contentPadding = innerPadding) {
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
        val align = if (isMyMessage) Alignment.End else Alignment.Start
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
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = align,
                ) {
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