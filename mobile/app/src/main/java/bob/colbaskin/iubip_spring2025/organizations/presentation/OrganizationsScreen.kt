package bob.colbaskin.iubip_spring2025.organizations.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.organizations.domain.models.Organization
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextFieldDefaults
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationsScreen(
    viewModel: OrganizationsViewModel = hiltViewModel(),
    onNavigateToOrganizationDetails: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleCreateDialog(true)
                    },
                    containerColor = ButtonColor,
                    contentColor = TextColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать организацию"
                    )
                }
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(onError = viewModel::loadOrganizations)
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onError = viewModel::loadOrganizations
            )
            else -> OrganizationsList(
                organizations = state.organizations,
                isAdmin = state.isAdmin,
                modifier = Modifier.padding(padding),
                onOrganizationSettings = onNavigateToOrganizationDetails
            )
        }
    }

    if (showCreateDialog) {
        CreateOrganizationDialog(
            onDismiss = { viewModel.toggleCreateDialog(false) },
            onCreate = { name, desc ->
                Log.d("Logging", "Create organization: $name, $desc")
                viewModel.createOrganization(name, desc)
            }
        )
    }
}

@Composable
private fun OrganizationsList(
    organizations: List<Organization>,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
    onOrganizationSettings: (String) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(organizations) { org ->
            OrganizationCard(
                organization = org,
                isAdmin = isAdmin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onSettingsClick = onOrganizationSettings
            )
        }
    }
}

@Composable
private fun OrganizationCard(
    organization: Organization,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
    onSettingsClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardColor,
            contentColor = TextColor
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColor,
                    modifier = Modifier.weight(1f)
                )
                if (isAdmin) {
                    IconButton(onClick = { onSettingsClick(organization.id.toString()) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = organization.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColor
                )
            }
        }
    }
}

@Composable
private fun CreateOrganizationDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создать организацию") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ButtonColor,
                        unfocusedContainerColor = CardColor,
                        disabledContainerColor = CardColor
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ButtonColor,
                        unfocusedContainerColor = CardColor,
                        disabledContainerColor = CardColor
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, description) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    contentColor = TextColor
                )
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor,
                    contentColor = TextColor
                )
            ) {
                Text("Отмена")
            }
        },
        containerColor = BackgroundColor,
    )
}