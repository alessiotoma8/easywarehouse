import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import easy.warehouse.ui.ScreenContent
import easy.warehouse.ui.WAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authAction: (String, String) -> Boolean, onBack: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) } // State for password visibility
    var isUsernameError by remember { mutableStateOf(false) } // State for username error
    var isPasswordError by remember { mutableStateOf(false) } // State for password error

    val (usernameFocusRequester, passwordFocusRequester) = remember { FocusRequester.createRefs() }

    Scaffold(
        topBar = {
            WAppBar("Login", onBack)
        }
    ) { innerPadding ->
        ScreenContent(innerPadding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.isCtrlPressed.not()) {
                            passwordFocusRequester.requestFocus()
                            true
                        } else if (it.key == Key.Enter) {
                            isUsernameError = username.isBlank()
                            isPasswordError = password.isBlank()

                            if (!isUsernameError && !isPasswordError) {
                                val isError = !authAction(username, password)
                                isPasswordError = isError
                                isUsernameError = isError
                            }
                            true
                        } else false
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Benvenuto!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        isUsernameError = false // Clear error when user types
                    },
                    label = { Text("Username") },
                    isError = isUsernameError, // Set error state
                    supportingText = {
                        if (isUsernameError && username.isBlank()) {
                            Text("Username richiesto")
                        } else if(isUsernameError){
                            Text("Username sbagliato")
                        }
                    },
                    modifier = Modifier.focusRequester(usernameFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isPasswordError = false // Clear error when user types
                    },
                    label = { Text("Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = isPasswordError, // Set error state
                    supportingText = {
                        if (isPasswordError && password.isBlank()) {
                            Text("Password richiesta")
                        }else if(isPasswordError){
                            Text("Password sbagliata")
                        }
                    },
                    trailingIcon = {
                        val image = if (showPassword)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (showPassword) "Hide password" else "Show password"
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    modifier = Modifier.focusRequester(passwordFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isUsernameError = username.isBlank()
                            isPasswordError = password.isBlank()

                            if (!isUsernameError && !isPasswordError) {
                                val isError = !authAction(username, password)
                                isPasswordError = isError
                                isUsernameError = isError
                            }

                        }
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        isUsernameError = username.isBlank()
                        isPasswordError = password.isBlank()

                        if (!isUsernameError && !isPasswordError) {
                            val isError = !authAction(username, password)
                            isPasswordError = isError
                            isUsernameError = isError
                        }
                    },
                    modifier = Modifier.size(200.dp, 50.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}