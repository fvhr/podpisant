package bob.colbaskin.iubip_spring2025.onboarding.presentation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.iubip_spring2025.R
import bob.colbaskin.iubip_spring2025.designsystem.Lottie
import bob.colbaskin.iubip_spring2025.designsystem.PagerWithIndicator
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 16.dp,
                        bottom = 32.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    OnBoardingPage.allPages[position].title,
                    color = TextColor,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Lottie(
                    lottieJson = OnBoardingPage.allPages[position].lottieJson,
                    modifier = Modifier.size(500.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    OnBoardingPage.allPages[position].label,
                    color = TextColor,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
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
    val title: String,
    @RawRes val lottieJson: Int,
    val label: String
) {
    data object First: OnBoardingPage(
        title = "Структура компании",
        lottieJson = R.raw.first,
        label = "Создавайте отделы, назначайте роли и контролируйте процессы за пару кликов"
    )
    data object Second: OnBoardingPage(
        title = "Подпись за секунды",
        lottieJson = R.raw.second,
        label = "Никакой бумаги — подписывайте документы через электронную почту"
    )
    data object Third: OnBoardingPage(
        title = "Всё готово!",
        lottieJson = R.raw.third,
        label = "Начните работать с документами быстрее и удобнее уже сегодня"
    )

    companion object {
        val allPages = listOf(First, Second, Third)
    }
}