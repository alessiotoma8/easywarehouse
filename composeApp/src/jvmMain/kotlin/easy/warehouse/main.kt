package easy.warehouse

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource


fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )
    val isAdmin = AccountManager.isAdmin
    Window(
        onCloseRequest = {
            if (isAdmin) exitApplication() // solo admin può chiudere
        },
        alwaysOnTop = !isAdmin,
        title = "easywarehouse",
        state = windowState,
        resizable = false,
        undecorated = !isAdmin
    ) {
        App()
    }
}

