package bob.colbaskin.iubip_spring2025.onboarding.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.iubip_spring2025.R
import bob.colbaskin.iubip_spring2025.designsystem.Lottie
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@Composable
fun WelcomeScreen(
    onNextScreen: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    Welcome(onNextScreen, viewModel::action)
}

@Composable
fun Welcome(
    onNextScreen: () -> Unit,
    dispatch: (OnBoardingAction) -> Unit
) {
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
            "Podpisant: Документы в цифре",
            color = TextColor,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Автоматизируйте подписание и управление документами!",
            color = TextColor,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.labelLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Lottie(R.raw.welcome, speed = 1.5f , modifier = Modifier.size(500.dp))
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                dispatch(OnBoardingAction.OnboardingInProgress)
                onNextScreen()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor,
                contentColor = TextColor
            )
        ) {
            Text("Начать")
        }
    }
}