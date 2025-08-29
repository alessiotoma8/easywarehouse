// App.kt
package easy.warehouse

import IdleDetector
import LoginScreen
import ReportsScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import easy.ui.theme.AppTheme
import easy.warehouse.ui.screen.AdminScreen
import easy.warehouse.ui.screen.WarehouseScreen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun App() {
    var showDialog by remember { mutableStateOf(false) }
    var idleTriggered by remember { mutableStateOf(false) }

    var isAdmin by remember { mutableStateOf(false) }

    AppTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showLoginScreen by remember { mutableStateOf(false) }
            var showReport by remember { mutableStateOf(false) }

            fun logout() {
                showLoginScreen = false
                isAdmin = false
                showReport = false
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {},
                    title = { Text("Sei inattivo") },
                    text = { Text("Logout automatico tra 3 secondi...") }
                )
            }
            LaunchedEffect(showDialog) {
                if (showDialog && idleTriggered) {
                    delay(3000)

                    // reset valori
                    showDialog = false
                    idleTriggered = false
                    logout()
                }
            }



            if (!showLoginScreen) {
                WarehouseScreen(onLoginClick = {
                    showLoginScreen = true
                })
            } else {
                IdleDetector(
                    timeout = 5.minutes, onIdle = {
                        showDialog = true
                        idleTriggered = true
                    }
                ) {
                    if (!isAdmin) {
                        LoginScreen(
                            authAction = { us, pwd ->
                                isAdmin = AccountManager.login(us, pwd)
                            },
                            onBack = {
                                showLoginScreen = false
                            }
                        )
                    } else if (!showReport) {
                        AdminScreen(onLogoutClick = {
                            logout()
                        }, onReportClick = {
                            showReport = true
                        })
                    } else {
                        ReportsScreen(onBackClick = { showReport = false })
                    }
                }
            }
        }
    }
}