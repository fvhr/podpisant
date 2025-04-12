package bob.colbaskin.iubip_spring2025.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bob.colbaskin.iubip_spring2025.R
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@Composable
fun ErrorScreen(
    message: String,
    onError: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Lottie(R.raw.error, modifier = Modifier.size(500.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ошибка: $message",
            color = Color.Red,
            style = TextStyle(
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onError() },
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor,
                contentColor = TextColor
            )
        ) {
            Text("Попробовать снова")
        }
    }
}