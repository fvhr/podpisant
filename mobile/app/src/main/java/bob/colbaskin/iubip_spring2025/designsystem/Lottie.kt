package bob.colbaskin.iubip_spring2025.designsystem

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun Lottie(
    @RawRes lottieJson: Int,
    modifier: Modifier = Modifier,
    speed: Float = 1f
) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieJson)
    )

    val compositionProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = speed,
        isPlaying = true
    )

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { compositionProgress }
    )
}