package bob.colbaskin.iubip_spring2025.organizations.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor
import bob.colbaskin.iubip_spring2025.ui.theme.BottomBarColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationDetailedScreen(
    organizationId: String,
    onBack: () -> Unit,
    viewModel: OrganizationDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(true) {
        viewModel.loadOrganization(organizationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.organization?.name ?: "Ищем организацию...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BottomBarColor,
                    titleContentColor = TextColor,
                    navigationIconContentColor = TextColor
                ),
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(
                onError = { viewModel.loadOrganization(organizationId) }
            )
            state.error != null -> ErrorScreen(
                message = state.error,
                onError = { viewModel.loadOrganization(organizationId) }
            )
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.organization?.id ?: "Нет данных",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}