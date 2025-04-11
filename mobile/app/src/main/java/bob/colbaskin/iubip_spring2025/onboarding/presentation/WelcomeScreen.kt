package bob.colbaskin.iubip_spring2025.onboarding.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
        Text("Добро пожаловать!")
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                dispatch(OnBoardingAction.OnboardingInProgress)
                onNextScreen()
            }
        ) {
            Text("Начать")
        }
    }
}