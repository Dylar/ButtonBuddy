package de.bitb.buttonbuddy.ui.buddies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.data.model.Buddy
import java.lang.Exception

import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.naviToBuddy
import de.bitb.buttonbuddy.ui.styles.createComposeView

@AndroidEntryPoint
class BuddiesFragment : Fragment() {

    private val viewModel: BuddiesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView { BuddiesScreen() }

    @Composable
    fun BuddiesScreen() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Buddies") }) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val token = viewModel.info.value?.token ?: throw Exception()
                        naviToBuddy(token)
                    }) { Icon(Icons.Filled.Info, contentDescription = "Show Info") }
            }
        ) { innerPadding ->
            val buddies = viewModel.buddies.observeAsState(emptyList())
            if (buddies.value.isEmpty()) {
                LoadingIndicator()
            } else {
                LazyColumn(contentPadding = innerPadding) {
                    items(buddies.value.size) { BuddyListItem(buddies.value[it]) }
                }
            }
        }
    }

    @Composable
    fun BuddyListItem(buddy: Buddy) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { naviToBuddy(buddy.token) })
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