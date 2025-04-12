package bob.colbaskin.iubip_spring2025.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bob.colbaskin.iubip_spring2025.R

@Composable
fun LoadingScreen(onError: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Lottie(lottieJson = R.raw.loading, speed = 3f)
    }
}