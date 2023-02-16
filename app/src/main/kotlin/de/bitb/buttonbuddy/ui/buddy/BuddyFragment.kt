package de.bitb.buttonbuddy.ui.buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.ui.composable.LoadingIndicator
import de.bitb.buttonbuddy.ui.styles.createComposeView

@AndroidEntryPoint
class BuddyFragment : Fragment() {

    private val viewModel: BuddyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = arguments?.getString("token") ?: throw Exception()
        viewModel.loadData(token)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =  createComposeView { BuddyScreen() }

    @Composable
    fun BuddyScreen() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Buddy") }) },
            content = {
                when (val buddy = viewModel.buddy.value) {
                    null -> LoadingIndicator()
                    else -> BuddyDetails(it,buddy)
                }
            },
        )
    }

    @Composable
    fun BuddyDetails(padding: PaddingValues, buddy: Buddy) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Vorname: ${buddy.firstName}")
            Text("Nachname: ${buddy.lastName}")
            if (viewModel.isMyself) Text("Token: ${buddy.token}")
        }
    }
}
