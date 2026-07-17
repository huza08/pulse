package app.pulse.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Sidebar(
    activeView: View,
    onNavigate: (View) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = Color(0xFF0d0d0d)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFF686868)
    val activeBg = Color(0xFF1a1a1a)
    val activeText = Color(0xFFf2f0eb)

    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(bg)
            .padding(top = 20.dp)
    ) {
        // Logo + app name
        Text(
            text = "Pulse",
            color = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Nav items
        View.entries.forEach { view ->
            val isActive = view == activeView
            NavItem(
                label = labelFor(view),
                isActive = isActive,
                onClick = { onNavigate(view) }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isActive) Color(0xFFf2f0eb) else Color(0xFF686868)
    val bgColor = if (isActive) Color(0xFF1a1a1a) else Color.Transparent
    val fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = fontWeight
        )
    }
}

private fun labelFor(view: View): String = when (view) {
    View.Home -> "Home"
    View.Search -> "Search"
    View.Songs -> "Songs"
    View.Artists -> "Artists"
    View.Albums -> "Albums"
    View.Playlists -> "Playlists"
}
