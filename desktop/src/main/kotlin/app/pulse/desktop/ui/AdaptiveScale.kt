package app.pulse.desktop.ui

import androidx.compose.ui.unit.Dp

fun adaptiveScale(windowWidthDp: Dp): Float {
    return (windowWidthDp.value / 960f).coerceIn(0.8f, 1.2f)
}
