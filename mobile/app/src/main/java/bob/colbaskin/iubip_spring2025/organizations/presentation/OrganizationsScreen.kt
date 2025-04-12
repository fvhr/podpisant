package bob.colbaskin.iubip_spring2025.organizations.presentation

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.tooling.preview.Preview
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor
import bob.colbaskin.iubip_spring2025.ui.theme.IUBIPSPRING2025Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationsScreen(
    viewModel: OrganizationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(
                    onClick = { viewModel.toggleCreateDialog(true) },
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
            state.isLoading -> CenterProgress()
            state.error != null -> ErrorMessage(state.error!!)
            else -> OrganizationsList(
                organizations = state.organizations,
                isAdmin = state.isAdmin,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showCreateDialog) {
        CreateOrganizationDialog(
            onDismiss = { viewModel.toggleCreateDialog(false) },
            onCreate = { name, desc -> viewModel.createOrganization(name, desc) }
        )
    }
}

@Composable
private fun OrganizationsList(
    organizations: List<Organization>,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(organizations) { org ->
            OrganizationCard(
                organization = org,
                isAdmin = isAdmin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun OrganizationCard(
    organization: Organization,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
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
                    IconButton(onClick = { }) {
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

@Composable
private fun CenterProgress() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Ошибка: $message",
            color = Color.Red
        )
    }
}