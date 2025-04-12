package bob.colbaskin.iubip_spring2025.auth.presentation

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmailInputScreen(
    onNextScreen: () -> Unit,
    onError: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val emailState = viewModel.email
    val isValidState = viewModel.isValid
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val authState = viewModel.authState.collectAsState().value
    when {
        authState.isLoading -> LoadingScreen(onError = onError)
        authState.error != null -> ErrorScreen(
            message = authState.error,
            onError = onError
        )
        else -> {
            EmailInputContent(
                onNextScreen = onNextScreen,
                viewModel = viewModel,
                emailState = emailState,
                isValidState = isValidState,
                scrollState = scrollState,
                focusManager = focusManager
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmailInputContent(
    onNextScreen: () -> Unit,
    viewModel: AuthViewModel,
    emailState: String,
    isValidState: Boolean,
    scrollState: ScrollState,
    focusManager: FocusManager,
) {
    val authState = viewModel.authState.collectAsState().value

    LaunchedEffect(authState.response) {
        Log.d("Logging", "Navigate to next screen from EmailInputContent with: ${viewModel.authState.value.response?.status} & ${viewModel.authState.value.response?.deviceId}")
        if (authState.response?.status == "success") {
            val deviceId = viewModel.authState.value.response?.deviceId ?: "ТУТ"
            viewModel.setDeviceLocalId(deviceId)
            onNextScreen()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp)
            .imePadding()
            .imeNestedScroll()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = emailState,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Введите email") },
                placeholder = { Text("youremail@gmail.com") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imeNestedScroll(),
                isError = !isValidState && emailState.isNotEmpty(),
                trailingIcon = {
                    if (!isValidState && emailState.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "error",
                            tint = Color.Red
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ButtonColor,
                    unfocusedContainerColor = CardColor,
                    disabledContainerColor = CardColor
                )
            )
        }
        IconButton(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd)
                .size(50.dp)
                .clip(RoundedCornerShape(50))
                .navigationBarsPadding(),
            onClick = { viewModel.loginByEmail(emailState) },
            enabled = isValidState,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = ButtonColor,
                disabledContainerColor = ButtonColor.copy(alpha = 0.3f)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "rightArrow"
            )
        }
    }
}
