// App.kt
package easy.warehouse

import LoginScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import easy.ui.theme.AppTheme
import easy.warehouse.ui.screen.AdminScreen
import easy.warehouse.ui.screen.WarehouseScreen

private val accountManager = AccountManager()

@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showLoginScreen by remember { mutableStateOf(false) }
            var isAdmin by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    showLoginScreen = !showLoginScreen
                    if (!showLoginScreen) {
                        isAdmin = false
                    }
                },
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isAdmin) {
                    Text("Esci")
                } else {
                    Text("Accedi")
                }
            }
            if (!showLoginScreen) {
                WarehouseScreen()
            } else if (!isAdmin) {
                LoginScreen { us, pwd ->
                    isAdmin = accountManager.login(us, pwd)
                }
            } else {
                AdminScreen()
            }
        }
    }
}