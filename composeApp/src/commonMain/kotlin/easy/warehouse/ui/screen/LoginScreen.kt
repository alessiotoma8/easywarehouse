import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import easy.warehouse.ui.ScreenContent
import easy.warehouse.ui.WAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authAction: (String, String) -> Unit, onBack: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Crea FocusRequester per i campi di testo
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
                            authAction(username, password)
                            true
                        } else false
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Campo Username: il focus viene richiesto all'avvio
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.focusRequester(usernameFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo Password: il focus viene richiesto al completamento del campo Username
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.focusRequester(passwordFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { authAction(username, password) }
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        authAction(username, password)
                    },
                    modifier = Modifier.size(200.dp, 50.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}
