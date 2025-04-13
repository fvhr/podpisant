package bob.colbaskin.iubip_spring2025.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.profile.domain.models.NotificationType
import bob.colbaskin.iubip_spring2025.profile.domain.models.Profile
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor
import bob.colbaskin.iubip_spring2025.ui.theme.BottomBarColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BottomBarColor,
                    titleContentColor = TextColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(onError = viewModel::loadProfile)
            state.error != null -> ErrorScreen(
                message = state.error,
                onError = viewModel::loadProfile,
            )
            else -> ProfileContent(
                profile = state.profile!!,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            ProfileCard(
                title = "Основная информация",
                items = listOf(
                    "ФИО" to profile.fullName,
                    "Email" to profile.email,
                    "Телефон" to profile.phone,
                    "Роль" to if (profile.isSuperAdmin) "Супер-админ" else "Пользователь"
                )
            )
        }

        item {
            ProfileCard(
                title = "Организации",
                items = listOf(
                    "Админ в" to profile.adminOrganizationId.toString(),
                    "Пользователь в" to profile.userOrganizations.joinToString()
                )
            )
        }

        item {
            ProfileCard(
                title = "Настройки",
                items = listOf(
                    "Уведомления" to when (profile.notificationType) {
                        NotificationType.TG -> "Telegram"
                        NotificationType.EMAIL -> "Email"
                        NotificationType.PHONE -> "Phone"
                    }
                )
            )
        }
    }
}

@Composable
private fun ProfileCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor,
            contentColor = TextColor
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            items.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        color = TextColor.copy(alpha = 0.8f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor
                    )
                }
            }
        }
    }
}