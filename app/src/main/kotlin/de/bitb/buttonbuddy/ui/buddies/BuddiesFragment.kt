package de.bitb.buttonbuddy.ui.buddies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.data.model.Buddy

import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.naviToBuddy
import de.bitb.buttonbuddy.ui.naviToScan
import de.bitb.buttonbuddy.ui.styles.createComposeView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BuddiesFragment : BaseFragment<BuddiesViewModel>() {

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
                    title = { Text("Buddies") },
                    actions = {
                        IconButton(onClick = {
                            lifecycleScope.launch {
                                val bla = viewModel.getInfo()
                                Log.e(toString(), "BLA uuid: ${bla?.uuid}")
                            }
                            val uuid = info?.uuid
                            Log.e(toString(), "info: ${info}")
                            Log.e(toString(), "uuid: ${uuid}")
                            if (uuid?.isNotBlank() == true) {
                                naviToBuddy(uuid)
                            } else {
                                showSnackBar("NO UUID")
                            }
                        }) { Icon(Icons.Default.Person, contentDescription = "Profil") }
                    })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { naviToScan() })
                { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Add Buddy") }
            }
        ) { innerPadding ->
            val buddies by viewModel.buddies.observeAsState(null)
            BuddiesList(innerPadding, buddies)
        }
    }

    @Composable
    fun BuddiesList(innerPadding: PaddingValues, buddies: List<Buddy>?) {
        when {
            buddies == null -> LoadingIndicator()
            buddies.isEmpty() ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(text = "Du hast keine Freunde")
                }
            else -> {
                LazyColumn(contentPadding = innerPadding) {
                    items(buddies.size) { BuddyListItem(buddies[it]) }
                }
            }
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
                Row {
                    Text("${buddy.firstName} ${buddy.lastName}")
                    Button(onClick = { viewModel.sendMessage(buddy) }) { Text("Send") }
                }
            }
        }
    }
}