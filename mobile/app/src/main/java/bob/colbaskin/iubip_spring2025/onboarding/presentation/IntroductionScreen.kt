package bob.colbaskin.iubip_spring2025.onboarding.presentation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.iubip_spring2025.R
import bob.colbaskin.iubip_spring2025.designsystem.Lottie
import bob.colbaskin.iubip_spring2025.designsystem.PagerWithIndicator
import kotlinx.coroutines.launch

@Composable
fun IntroductionScreen(
    onNextScreen: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    OnBoarding(onNextScreen, viewModel::action)
}

@Composable
private fun OnBoarding(
    onNextScreen: () -> Unit,
    dispatch: (OnBoardingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()
        val pageCount = OnBoardingPage.allPages.size
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { pageCount }
        )
        val buttonText = if (pagerState.currentPage == pageCount - 1) "Начать!" else "Дальше"
        PagerWithIndicator(
            pageCount = pageCount,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp),
            pagerState = pagerState
        ) { position ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Lottie(
                    lottieJson = OnBoardingPage.allPages[position].lottieJson,
                    modifier = Modifier.size(150.dp)
                )
            }
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pageCount - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        dispatch(OnBoardingAction.OnboardingComplete)
                        onNextScreen()
                    }
                }
            }
        ) {
            Text(
                text = buttonText
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = {
                dispatch(OnBoardingAction.OnboardingComplete)
                onNextScreen()
            },
        ) {
            Text(text = "Пропустить")
        }
    }
}

private sealed class OnBoardingPage(
    @RawRes val lottieJson: Int
) {
    data object First: OnBoardingPage(lottieJson = R.raw.one)
    data object Second: OnBoardingPage(lottieJson = R.raw.two)
    data object Third: OnBoardingPage(lottieJson = R.raw.three)

    companion object {
        val allPages = listOf(First, Second, Third)
    }
}