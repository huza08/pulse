package app.pulse.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.View

@Composable
fun TopNavBar(
    onNavigate: (View) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = Color(0xFF0a0a0a)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFF686868)
    val surface = Color(0xFF1e1e1e)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(bg)
            .padding(horizontal = 16.dp)
    ) {
        // Left: navigation arrows
        Icon(
            painter = painterResource("/icons/chevron_back.svg"),
            contentDescription = "Back",
            tint = text,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onNavigate(View.Home) }
                )
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource("/icons/chevron_forward.svg"),
            contentDescription = "Forward",
            tint = dim.copy(alpha = 0.4f),
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(24.dp))

        // Center: search pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onNavigate(View.Search) }
                )
                .padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource("/icons/search.svg"),
                contentDescription = "Search",
                tint = dim,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "What do you want to play?",
                color = dim,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.width(16.dp))

        // Right: profile icon
        Icon(
            painter = painterResource("/icons/person.svg"),
            contentDescription = "Profile",
            tint = text,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF1e1e1e))
                .padding(5.dp)
        )
    }
}
