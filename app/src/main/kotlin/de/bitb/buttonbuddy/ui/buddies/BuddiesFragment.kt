package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.DEFAULT_COOLDOWN
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.ui.base.*
import de.bitb.buttonbuddy.ui.base.composable.CoolDownButton
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.info.InfoDialog
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BuddiesFragment : BaseFragment<BuddiesViewModel>() {
    companion object {
        const val APPBAR_TAG = "BuddiesAppbar"
        const val MENU_BUTTON_TAG = "BuddiesMenuButton"
        const val DRAWER_INFO_BUTTON_TAG = "BuddiesInfoButton"
        const val DRAWER_PROFILE_BUTTON_TAG = "BuddiesProfileButton"
        const val DRAWER_LOGOUT_BUTTON_TAG = "BuddiesLogoutButton"
        const val DRAWER_SETTINGS_BUTTON_TAG = "BuddiesSettingsButton"

        const val SCAN_BUTTON_TAG = "BuddiesScanButton"

        const val LIST_TAG = "BuddiesList"
        const val REFRESH_INDICATOR_TAG = "BuddiesRefreshingIndicator"

        fun buddySendButtonTag(buddy: Buddy): String =
            "BuddiesSendButton ${buddy.fullName}".replace(" ", "-")
    }

    override val viewModel: BuddiesViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val showDialog = remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.buddies_title)) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { scaffoldState.drawerState.open() } },
                            modifier = Modifier.testTag(MENU_BUTTON_TAG)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Toggle drawer"
                            )
                        }
                    }
                )
            },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = {
                DrawerHeader()
                DrawerBody(
                    // TODO back button not correct
                    items = listOf(
                        MenuItem(
                            tag = DRAWER_PROFILE_BUTTON_TAG,
                            title = stringResource(R.string.profile_title),
                            contentDescription = "Profile",
                            icon = Icons.Default.Person,
                            onTap = ::naviToProfile,
                        ),
                        MenuItem(
                            tag = DRAWER_LOGOUT_BUTTON_TAG,
                            title = stringResource(R.string.logout_title),
                            contentDescription = "Logout",
                            icon = Icons.Default.Info,
                            onTap = viewModel::logout,
                        ),
                        MenuItem(
                            tag = DRAWER_SETTINGS_BUTTON_TAG,
                            title = stringResource(R.string.settings_title),
                            contentDescription = "Settings screen button",
                            icon = Icons.Default.Settings,
                            onTap = ::naviBuddysToSettings,
                        ),
                        MenuItem(
                            tag = DRAWER_INFO_BUTTON_TAG,
                            title = stringResource(R.string.info_title),
                            contentDescription = "Information dialog",
                            icon = Icons.Default.Info,
                            onTap = { showDialog.value = !showDialog.value }
                        ),
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.testTag(SCAN_BUTTON_TAG),
                    onClick = ::naviToScan
                ) { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan Buddy") }
            }
        ) { innerPadding ->
            val buddies by viewModel.buddies.observeAsState(null)
            BuddiesList(innerPadding, buddies)
        }

        if (showDialog.value) {
            InfoDialog { showDialog.value = false }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BuddiesList(innerPadding: PaddingValues, buddies: List<Buddy>?) {
        val refreshing by remember { viewModel.isRefreshing }
        val state = rememberPullRefreshState(refreshing, viewModel::refreshData)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .pullRefresh(state)
                .padding(innerPadding)
        ) {
            when {
                buddies == null -> LoadingIndicator()
                buddies.isEmpty() -> Text(
                    modifier = Modifier.fillMaxSize(),
                    text = getString(R.string.no_buddies)
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(LIST_TAG),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = innerPadding
                    ) { items(buddies.size) { BuddyListItem(buddies[it]) } }
                }
            }
            PullRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .testTag(REFRESH_INDICATOR_TAG),
                refreshing = refreshing,
                state = state
            )
        }
    }

    @Composable
    fun BuddyListItem(buddy: Buddy) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .padding(8.dp)
                .clickable { naviToBuddy(buddy.uuid) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    buddy.fullName,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
                val lastMsg by viewModel.getLastMessage(buddy.uuid).observeAsState()
                val settings by viewModel.settingsRepo.getLiveSettings().observeAsState()
                val cooldown = settings?.buddysCooldown?.get(buddy.uuid) ?: DEFAULT_COOLDOWN
                val lastMsgDate = lastMsg?.date ?: Date(0)
                CoolDownButton(lastMsgDate, cooldown)
                {
                    FloatingActionButton(
                        modifier = Modifier.testTag(buddySendButtonTag(buddy)),
                        onClick = { viewModel.sendMessageToBuddy(buddy) }
                    ) { Icon(Icons.Filled.Send, contentDescription = "Send") }
                }
            }
        }
    }
}