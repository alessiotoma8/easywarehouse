package easy.warehouse

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )
    val isAdmin = AccountManager.isAdmin
    Window(
        onCloseRequest = {
            if (isAdmin) exitApplication() // solo admin può chiudere
        },
        title = "easywarehouse",
        state = windowState,
        resizable = false,
        undecorated = !isAdmin
    ) {
        App()
    }
}