package bob.colbaskin.iubip_spring2025.auth.presentation.otp

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.auth.presentation.AuthViewModel
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@Composable
fun OtpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNextScreen: () -> Unit
) {
    OtpScreenFull(
        viewModel = viewModel,
        onNextScreen = onNextScreen
    )
}

@Composable
fun OtpScreenFull(
    viewModel: AuthViewModel,
    onNextScreen: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val focusRequesters = remember {
        List(4) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.focusedIndex) {
        state.focusedIndex?.let { index ->
            focusRequesters.getOrNull(index)?.requestFocus()
        }
    }

    LaunchedEffect(state.code, keyboardManager) {
        val allNumbersEntered = state.code.none { it == null }
        if(allNumbersEntered) {
            focusRequesters.forEach {
                it.freeFocus()
            }
            focusManager.clearFocus()
            keyboardManager?.hide()
        }
    }

    OtpContent(
        state = state,
        focusRequesters = focusRequesters,
        dispatch = viewModel::action,
        onAction = { action ->
            when(action) {
                is OtpAction.OnEnterNumber -> {
                    if(action.number != null) {
                        focusRequesters[action.index].freeFocus()
                    }
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onNextScreen = onNextScreen,
        modifier = Modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OtpContent(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    dispatch: (AuthAction) -> Unit,
    onNextScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .imeNestedScroll()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Row(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.code.forEachIndexed { index, number ->
                    OtpInputField(
                        number = number,
                        focusRequester = focusRequesters[index],
                        onFocusChanged = { isFocused ->
                            if (isFocused) onAction(OtpAction.OnChangeFieldFocused(index))
                        },
                        onNumberChanged = { newNumber ->
                            onAction(OtpAction.OnEnterNumber(newNumber, index))
                        },
                        onKeyboardBack = {
                            onAction(OtpAction.OnKeyboardBack)
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .imeNestedScroll()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            state.isValid?.let { isValid ->
                Text(
                    text = if (isValid) "Код подтверждения верный!"
                    else "Неверный код подтверждения!",
                    color = if (isValid) TextColor else Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )

                if (isValid) {
                    LaunchedEffect(Unit) {
                        onNextScreen()
                        dispatch(AuthAction.Authenticated)
                    }
                }
            }

            TextButton(
                onClick = { /* ЩЭ РАЗ! */ },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Отправить код еще раз?",
                    color = TextColor,
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    ),
                )
            }
        }
    }
}