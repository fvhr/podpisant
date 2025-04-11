package bob.colbaskin.iubip_spring2025.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor

@Composable
fun EmailInputScreen(
    onNextScreen: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Введите email") },
            placeholder = { Text("youremail@gmail.com") },
            singleLine = true,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd)
                .size(50.dp)
                .clip(RoundedCornerShape(50)),
            onClick = onNextScreen,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = ButtonColor
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "rightArrow",
                modifier = Modifier
            )
        }
    }
}
