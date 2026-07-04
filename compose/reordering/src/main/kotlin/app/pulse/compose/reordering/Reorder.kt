package app.pulse.compose.reordering

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.reorder(
    reorderingState: ReorderingState,
    index: Int
) = this.pointerInput(reorderingState) {
    detectDragGestures(
        onDragStart = { reorderingState.onDragStart(index) },
        onDrag = reorderingState::onDrag,
        onDragEnd = reorderingState::onDragEnd,
        onDragCancel = reorderingState::onDragEnd
    )
}
