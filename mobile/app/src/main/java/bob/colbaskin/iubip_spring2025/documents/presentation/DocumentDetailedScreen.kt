package bob.colbaskin.iubip_spring2025.documents.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.MainActivity
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor
import bob.colbaskin.iubip_spring2025.ui.theme.BottomBarColor
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailedScreen(
    documentId: Int,
    viewModel: DocumentDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.document?.name ?: "Ищем документ...",
                        color = TextColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Назад",
                            tint = TextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BottomBarColor,
                    titleContentColor = TextColor,
                    navigationIconContentColor = TextColor
                )
            )
        },
        containerColor = BackgroundColor,
        contentColor = TextColor
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(onError = { viewModel.loadDocument(documentId) })
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onError = { viewModel.loadDocument(documentId) }
            )
            else -> {
                Column(Modifier.padding(padding)) {
                    state.document?.let { doc ->
                        DocumentInfo(doc)
                        ActionButtons(viewModel, doc.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentInfo(document: Document) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor,
            contentColor = TextColor
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Создатель: ${document.creatorId}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, document.status.color, CircleShape)
                )
            }
            Text(
                "Дата создания: ${document.createdAt}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Статус: ${getStatusName(document.status)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            document.type?.let {
                Text(
                    "Тип: ${getTypeName(it)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    viewModel: DocumentDetailsViewModel,
    status: DocumentStatus
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            DocumentStatus.REJECTED -> {
                Button(
                    onClick = viewModel::signDocument,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor,
                        contentColor = TextColor
                    )
                ) {
                    Text("Подписать")
                }
            }
            DocumentStatus.IN_PROGRESS -> {
                OutlinedButton(
                    onClick = viewModel::rejectDocument,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    )
                ) {
                    Text("Отклонить")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = viewModel::signDocument,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor,
                        contentColor = TextColor
                    )
                ) {
                    Text("Подтвердить")
                }
            }
            else -> {}
        }
    }
}

private fun getStatusName(status: DocumentStatus): String = when(status) {
    DocumentStatus.IN_PROGRESS -> "В процессе"
    DocumentStatus.SIGNED -> "Подписано"
    DocumentStatus.REJECTED -> "Отклонено"
    DocumentStatus.ALL -> "Все статусы"
}

private fun getTypeName(type: Document.DocumentType): String = when(type) {
    Document.DocumentType.STRICT -> "Строгий"
    Document.DocumentType.REGULAR -> "Обычный"
}