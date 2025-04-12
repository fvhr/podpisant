package bob.colbaskin.iubip_spring2025.auth.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmailInputScreen(
    onNextScreen: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val emailState = viewModel.email
    val isValidState = viewModel.isValid
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
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
            onClick = onNextScreen,
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
