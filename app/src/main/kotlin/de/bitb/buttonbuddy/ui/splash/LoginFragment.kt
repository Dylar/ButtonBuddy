package de.bitb.buttonbuddy.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.ui.naviToBuddy
import de.bitb.buttonbuddy.ui.styles.createComposeView
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = createComposeView {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Login") }) },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(all = 32.dp),
                    onClick = { viewModel.login() }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Login")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                    value = viewModel.firstName,
                    onValueChange = { viewModel.firstName = it },
                    label = { Text("First name") },
                )
                OutlinedTextField(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    value = viewModel.lastName,
                    onValueChange = { viewModel.lastName = it },
                    label = { Text("Last name") }
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                viewModel.error?.let { Text(it.asString()) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigate = { id ->
            findNavController(this).navigate(id)
        }
    }
}