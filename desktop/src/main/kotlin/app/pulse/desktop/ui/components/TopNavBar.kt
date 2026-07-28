package app.pulse.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.View
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.constants.sizes.Sizes

@Composable
fun TopNavBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigate: (View) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = Color(0xFF0a0a0a)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFF686868)
    val surface = Color(0xFF1e1e1e)
    val fieldText = Color(0xFFe0ddd7)
    val fieldPlaceholder = Color(0xFF686868)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(Sizes.topBarHeight.dp)
            .background(bg)
            .padding(horizontal = Sizes.rightPanelPadding.dp)
    ) {
        // searchbar
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = query,
                onValueChange = { onQueryChange(it) },
                placeholder = {
                    Text(
                        text = "What do you want to play?",
                        color = fieldPlaceholder,
                        fontSize = FontSizes.searchbar.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource("/icons/search.svg"),
                        contentDescription = "Search",
                        tint = dim,
                        modifier = Modifier.size(Sizes.searchIconSize.dp)
                    )
                },
                singleLine = true,
                textStyle = TextStyle(color = fieldText, fontSize = FontSizes.searchbar.sp, fontWeight = FontWeight.Medium),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onNavigate(View.Search) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = fieldText,
                    unfocusedTextColor = fieldText,
                    cursorColor = text,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = surface,
                    unfocusedContainerColor = surface
                ),
                shape = RoundedCornerShape(Sizes.searchCornerRadius.dp),
                modifier = Modifier
                    .widthIn(max = Sizes.searchMaxWidth.dp)
                    .fillMaxWidth()
                    .padding(top = Sizes.searchTopPad.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(Modifier.width(Sizes.rightPanelPadding.dp))

        // dummy for now
        Icon(
            painter = painterResource("/icons/person.svg"),
            contentDescription = "Profile",
            tint = text,
            modifier = Modifier
                .size(Sizes.profileIconSize.dp)
                .clip(CircleShape)
                .background(Color(0xFF1e1e1e))
                .padding(5.dp)
        )
    }
}
