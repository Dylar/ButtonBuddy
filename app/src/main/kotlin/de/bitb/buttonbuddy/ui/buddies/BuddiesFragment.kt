package de.bitb.buttonbuddy.ui.buddies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.base.naviToBuddy
import de.bitb.buttonbuddy.ui.base.naviToScan
import de.bitb.buttonbuddy.ui.base.styles.createComposeView

@AndroidEntryPoint
class BuddiesFragment : BaseFragment<BuddiesViewModel>() {
    companion object {
        const val APPBAR_TAG = "BuddiesFragmentAppbar"
    }

    override val viewModel: BuddiesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        val info by viewModel.info.observeAsState(null)
        BuddiesScreen(info)
    }

    @Composable
    fun BuddiesScreen(info: Info?) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.buddies_title)) },
                    actions = {
                        IconButton(onClick = {
                            val uuid = info?.uuid
                            if (uuid?.isNotBlank() == true) {
                                naviToBuddy(uuid)
                            } else {
                                showSnackBar("NO UUID")
                            }
                        }) { Icon(Icons.Default.Person, contentDescription = "Profil") }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { naviToScan() })
                { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan Buddy") }
            }
        ) { innerPadding ->
            val buddies by viewModel.buddies.observeAsState(null)
            BuddiesList(innerPadding, buddies)
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                buddies == null -> LoadingIndicator()
                buddies.isEmpty() ->
                    Text(text = "Du hast keine Freunde")
                else -> {
                    LazyColumn(contentPadding = innerPadding) {
                        items(buddies.size) { BuddyListItem(buddies[it]) }
                    }
                }
            }
            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = refreshing,
                state = state
            )
        }
    }

    @Composable
    fun BuddyListItem(buddy: Buddy) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { naviToBuddy(buddy.uuid) })
        {
            Card(
                elevation = 4.dp, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text("${buddy.firstName} ${buddy.lastName}")
                    Button(onClick = { viewModel.sendMessage(buddy) }) { Text("Send") }
                }
            }
        }
    }
}