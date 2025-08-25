// App.kt
package easy.warehouse

import LoginScreen
import ReportsScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
            var showReport by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdmin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showLoginScreen = !showLoginScreen
                        if (!showLoginScreen) {
                            isAdmin = false
                            showReport = false
                        }
                    }
                ) {
                    if (isAdmin) {
                        Text("Esci")
                    } else {
                        Text("Accedi")
                    }
                }

                if (isAdmin) {
                    Button(
                        onClick = {
                            showReport = !showReport
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                    ) {
                        Text("Report")
                    }
                }
            }

            if (!showLoginScreen) {
                WarehouseScreen()
            } else if (!isAdmin) {
                LoginScreen { us, pwd ->
                    isAdmin = accountManager.login(us, pwd)
                }
            } else if (!showReport) {
                AdminScreen()
            } else {
                ReportsScreen()
            }
        }
    }
}