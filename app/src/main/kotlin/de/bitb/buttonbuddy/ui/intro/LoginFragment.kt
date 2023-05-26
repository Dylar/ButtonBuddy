package de.bitb.buttonbuddy.ui.intro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.naviToRegister
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import de.bitb.buttonbuddy.ui.info.InfoDialog
import de.bitb.buttonbuddy.usecase.user.LoginResponse

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {
    companion object {
        const val APPBAR_TAG = "LoginAppbar"
        const val INFO_BUTTON_TAG = "LoginInfoButton"
        const val EMAIL_TAG = "LoginEmail"
        const val PW_TAG = "LoginPW"
        const val REGISTER_BUTTON_TAG = "LoginRegisterButton"
        const val LOGIN_BUTTON_TAG = "LoginButton"
        const val ERROR_TAG = "LoginError"
    }

    override val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        var showDialog by remember { mutableStateOf(false) }
        var passwordVisibility by remember { mutableStateOf(false) }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.login_title)) },
                    actions = {
                        IconButton(
                            modifier = Modifier.testTag(INFO_BUTTON_TAG),
                            onClick = { showDialog = !showDialog }
                        ) { Icon(Icons.Default.Info, contentDescription = "Info") }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(all = 32.dp)
                        .testTag(LOGIN_BUTTON_TAG),
                    onClick = { viewModel.login() },
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colors.onSurface
                        )
                    } else {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Login")
                    }
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
                    isError = viewModel.error is LoginResponse.EmailError,
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .testTag(EMAIL_TAG),
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text(getString(R.string.email)) },
                )
                OutlinedTextField(
                    isError = viewModel.error is LoginResponse.PwEmpty,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW_TAG),
                    value = viewModel.pw,
                    onValueChange = { viewModel.pw = it },
                    label = { Text(getString(R.string.pw1_label)) },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisibility = !passwordVisibility },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            val icon =
                                if (passwordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            Icon(icon, contentDescription = "Toggle password visibility")
                        }
                    },
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                viewModel.error?.let {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag(ERROR_TAG),
                        contentAlignment = Alignment.TopCenter,
                    ) { Text(it.message.asString(), color = BaseColors.FireRed) }
                }
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(REGISTER_BUTTON_TAG),
                    onClick = ::naviToRegister,
                    content = {
                        Text(
                            text = getString(R.string.login_register_account),
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        }

        if (showDialog) {
            InfoDialog { showDialog = false }
        }
    }
}