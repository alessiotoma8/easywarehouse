package easy.warehouse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource


fun main() = application {
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )

    Window(
        onCloseRequest = {
            if (isAdmin) exitApplication() // solo admin può chiudere
        },
        alwaysOnTop = false,//!isAdmin,
        title = "easywarehouse",
        state = windowState,
        resizable = false,//isAdmin,
        undecorated = false//!isAdmin
    ) {
        App(
            isAdmin = isAdmin,
            onAdminChange = { isAdminNew ->
                isAdmin = isAdminNew
            }
        )
    }
}

