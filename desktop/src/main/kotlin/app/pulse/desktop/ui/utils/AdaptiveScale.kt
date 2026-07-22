package app.pulse.desktop.ui.utils

import androidx.compose.ui.unit.Dp

fun adaptiveScale(windowWidthDp: Dp): Float {
    return (windowWidthDp.value / 960f).coerceIn(0.8f, 1f)
}
