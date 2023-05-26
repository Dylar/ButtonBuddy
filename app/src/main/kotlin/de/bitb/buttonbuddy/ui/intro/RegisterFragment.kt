package de.bitb.buttonbuddy.ui.intro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import de.bitb.buttonbuddy.usecase.user.RegisterResponse

@AndroidEntryPoint
class RegisterFragment : BaseFragment<RegisterViewModel>() {
    companion object {
        const val APPBAR_TAG = "RegisterAppbar"
        const val FIRST_NAME_TAG = "RegisterFirstName"
        const val LAST_NAME_TAG = "RegisterLastName"
        const val EMAIL_TAG = "RegisterEmail"
        const val PW1_TAG = "RegisterPW1"
        const val PW2_TAG = "RegisterPW2"
        const val REGISTER_BUTTON_TAG = "RegisterButton"
        const val ERROR_TAG = "RegisterError"
    }

    override val viewModel: RegisterViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        var passwordVisibility by remember { mutableStateOf(false) }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    modifier = Modifier.testTag(APPBAR_TAG),
                    title = { Text(getString(R.string.register_title)) })
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(all = 32.dp)
                        .testTag(REGISTER_BUTTON_TAG),
                    onClick = { viewModel.register() }
                ) { Icon(Icons.Default.ArrowForward, contentDescription = "Register") }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    isError = viewModel.error == RegisterResponse.FirstNameEmpty,
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .testTag(FIRST_NAME_TAG),
                    value = viewModel.firstName,
                    onValueChange = { viewModel.firstName = it },
                    label = { Text(getString(R.string.first_name)) },
                )
                OutlinedTextField(
                    isError = viewModel.error == RegisterResponse.LastNameEmpty,
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(LAST_NAME_TAG),
                    value = viewModel.lastName,
                    onValueChange = { viewModel.lastName = it },
                    label = { Text(getString(R.string.last_name)) }
                )
                OutlinedTextField(
                    isError = viewModel.error is RegisterResponse.EmailError,
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(EMAIL_TAG),
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text(getString(R.string.email)) }
                )
                OutlinedTextField(
                    isError = viewModel.error is RegisterResponse.PWError,
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW1_TAG),
                    value = viewModel.pw1,
                    onValueChange = { viewModel.pw1 = it },
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
                OutlinedTextField(
                    isError = viewModel.error is RegisterResponse.PWError,
                    singleLine = true,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .testTag(PW2_TAG),
                    value = viewModel.pw2,
                    onValueChange = { viewModel.pw2 = it },
                    label = { Text(getString(R.string.pw2_label)) },
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
                Spacer(modifier = Modifier.padding(top = 100.dp))
            }
        }
    }
}