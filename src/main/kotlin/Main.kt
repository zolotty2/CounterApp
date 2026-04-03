import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import models.User

fun main() = application {
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 800.dp,
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Система управления вендинговыми аппаратами",
        state = windowState
    ) {
        MaterialTheme {
            if (!isLoggedIn) {
                LoginScreen(
                    onLoginSuccess = { user ->
                        currentUser = user
                        isLoggedIn = true
                    }
                )
            } else {
                MainAppScreen(
                    user = currentUser!!,
                    onLogout = {
                        isLoggedIn = false
                        currentUser = null
                    }
                )
            }
        }
    }
}