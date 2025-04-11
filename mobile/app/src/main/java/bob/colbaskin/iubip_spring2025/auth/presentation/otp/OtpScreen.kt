package bob.colbaskin.iubip_spring2025.auth.presentation.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.auth.presentation.AuthViewModel
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.IUBIPSPRING2025Theme

@Composable
fun OtpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
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

@Composable
fun OtpContent(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    onNextScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            state.code.forEachIndexed { index, number ->
                OtpInputField(
                    number = number,
                    focusRequester = focusRequesters[index],
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            onAction(OtpAction.OnChangeFieldFocused(index))
                        }
                    },
                    onNumberChanged = { newNumber ->
                        onAction(OtpAction.OnEnterNumber(newNumber, index))
                    },
                    onKeyboardBack = {
                        onAction(OtpAction.OnKeyboardBack)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }
        state.isValid?.let { isValid ->
            Text(
                text = if (isValid) "OTP is valid!" else "OTP is invalid!",
                color = if (isValid) ButtonColor else Color.Red,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(
                modifier = Modifier
                    .padding(32.dp)
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
}

@Preview
@Composable
private fun OtpScreenPreview() {
    IUBIPSPRING2025Theme {
        OtpScreen(
            onNextScreen = {}
        )
    }
}