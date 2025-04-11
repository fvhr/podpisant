package bob.colbaskin.iubip_spring2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import bob.colbaskin.iubip_spring2025.common.MainViewModel
import bob.colbaskin.iubip_spring2025.common.models.UiState
import bob.colbaskin.iubip_spring2025.navigation.AppNavHost
import bob.colbaskin.iubip_spring2025.ui.theme.IUBIPSPRING2025Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uiState: UiState by mutableStateOf(UiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState
                    .onEach { uiState = it}
                    .collect {}
            }
        }

        enableEdgeToEdge()
        setContent {
            IUBIPSPRING2025Theme {
                if (uiState is UiState.Success) {
                    AppNavHost(uiState as UiState.Success)
                }
            }
        }
    }
}
