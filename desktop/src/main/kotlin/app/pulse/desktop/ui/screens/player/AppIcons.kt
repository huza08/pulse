package app.pulse.desktop.ui.screens.player

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Custom icons matching Android's IonIcons-style XML drawables. */
object AppIcons {

    /** Filled triangle play button (matches play.xml). */
    val Play: ImageVector = ImageVector.Builder(
        name = "Play",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 4f,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(8f, 5f)
            lineTo(8f, 19f)
            lineTo(20f, 12f)
            close()
        }
    }.build()

    /** Two rounded vertical bars (matches pause.xml). */
    val Pause: ImageVector = ImageVector.Builder(
        name = "Pause",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // Left bar (rounded caps approximated with short line segments)
            moveTo(7.25f, 4f)
            lineTo(9.25f, 4f)
            lineTo(10.75f, 5.5f)
            lineTo(10.75f, 18.5f)
            lineTo(9.25f, 20f)
            lineTo(7.25f, 20f)
            lineTo(5.75f, 18.5f)
            lineTo(5.75f, 5.5f)
            close()
            // Right bar
            moveTo(14.75f, 4f)
            lineTo(16.75f, 4f)
            lineTo(18.25f, 5.5f)
            lineTo(18.25f, 18.5f)
            lineTo(16.75f, 20f)
            lineTo(14.75f, 20f)
            lineTo(13.25f, 18.5f)
            lineTo(13.25f, 5.5f)
            close()
        }
    }.build()

    /** Two left-pointing triangles — skip to previous track (matches play_skip_back.xml). */
    val SkipBack: ImageVector = ImageVector.Builder(
        name = "SkipBack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Left triangle
            moveTo(12f, 5f)
            lineTo(12f, 19f)
            lineTo(5f, 12f)
            close()
            // Right triangle
            moveTo(19f, 5f)
            lineTo(19f, 19f)
            lineTo(12f, 12f)
            close()
        }
    }.build()

    /** Two right-pointing triangles — skip to next track (matches play_skip_forward.xml). */
    val SkipForward: ImageVector = ImageVector.Builder(
        name = "SkipForward",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Left triangle
            moveTo(5f, 5f)
            lineTo(5f, 19f)
            lineTo(12f, 12f)
            close()
            // Right triangle
            moveTo(12f, 5f)
            lineTo(12f, 19f)
            lineTo(19f, 12f)
            close()
        }
    }.build()

    /** Single left-pointing triangle — rewind 10s (matches play_skip style). */
    val Rewind10: ImageVector = ImageVector.Builder(
        name = "Rewind10",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(15f, 5f)
            lineTo(15f, 19f)
            lineTo(6f, 12f)
            close()
        }
    }.build()

    /** Single right-pointing triangle — forward 10s (matches play_skip style). */
    val Forward10: ImageVector = ImageVector.Builder(
        name = "Forward10",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 5f)
            lineTo(9f, 19f)
            lineTo(18f, 12f)
            close()
        }
    }.build()

    /** Speaker with two sound wave arcs (matches volume_up.xml). */
    val VolumeUp: ImageVector = ImageVector.Builder(
        name = "VolumeUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Speaker body
        path(
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineJoin = StrokeJoin.Round,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(7.5f, 9f)
            lineTo(7.5f, 15f)
            lineTo(10.5f, 15f)
            lineTo(14.5f, 19f)
            lineTo(14.5f, 5f)
            lineTo(10.5f, 9f)
            close()
        }
        // Small sound wave (straight line approximation)
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(17.5f, 9.5f)
            lineTo(19f, 12f)
            lineTo(17.5f, 14.5f)
        }
        // Large sound wave (straight line approximation)
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(20f, 7.5f)
            lineTo(22.5f, 12f)
            lineTo(20f, 16.5f)
        }
    }.build()

    /** Two arrows forming a loop (matches repeat.xml). */
    val Repeat: ImageVector = ImageVector.Builder(
        name = "Repeat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            // Top arrow: rightward → down → left
            moveTo(7f, 7f)
            lineTo(17f, 7f)
            lineTo(17f, 10f)
            lineTo(21f, 6f)
            lineTo(17f, 2f)
            lineTo(17f, 5f)
            lineTo(5f, 5f)
            lineTo(5f, 11f)
            lineTo(7f, 11f)
            close()
            // Bottom arrow: leftward → up → right (completing the loop)
            moveTo(17f, 17f)
            lineTo(7f, 17f)
            lineTo(7f, 14f)
            lineTo(3f, 18f)
            lineTo(7f, 22f)
            lineTo(7f, 19f)
            lineTo(19f, 19f)
            lineTo(19f, 13f)
            lineTo(17f, 13f)
            close()
        }
    }.build()
}
