import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun IdleDetector(
    timeout: Duration = 5.minutes,
    onIdle: () -> Unit,
    content: @Composable () -> Unit
) {
    val lastActivity = remember { mutableStateOf(Clock.System.now()) }

    val touchOrPointerModifier = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent()
                lastActivity.value = Clock.System.now()
            }
        }
    }

    val keyModifier = Modifier.onKeyEvent {
        lastActivity.value = Clock.System.now()
        false
    }

    LaunchedEffect(Unit) {
        while (true) {
            val now: Instant = Clock.System.now()
            val elapsed: Duration = now - lastActivity.value

            if (elapsed >= timeout) {
                onIdle()
                lastActivity.value = now // reset timer
            }

            delay(10.seconds) // controlla ogni secondo
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(touchOrPointerModifier)
            .then(keyModifier)
    ) {
        content()
    }
}
