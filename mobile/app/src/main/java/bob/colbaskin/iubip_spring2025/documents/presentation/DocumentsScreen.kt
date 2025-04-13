package bob.colbaskin.iubip_spring2025.documents.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.ui.theme.BottomBarColor
import bob.colbaskin.iubip_spring2025.ui.theme.ButtonColor
import bob.colbaskin.iubip_spring2025.ui.theme.CardColor
import bob.colbaskin.iubip_spring2025.ui.theme.TextColor
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import bob.colbaskin.iubip_spring2025.designsystem.ErrorScreen
import bob.colbaskin.iubip_spring2025.designsystem.LoadingScreen
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus
import bob.colbaskin.iubip_spring2025.ui.theme.BackgroundColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel = hiltViewModel(),
    onDocumentClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState()
    val filters = DocumentStatus.entries.take(4)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FilterRow(
                selectedFilter = filters[pagerState.currentPage],
                onFilterSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                pagerState = pagerState
            )
        },
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(
                    onClick = { viewModel.toggleCreateDialog(true) },
                    containerColor = ButtonColor
                ) {
                    Icon(Icons.Default.Add, "Создать документ")
                }
            }
        },
        containerColor = BackgroundColor,
        contentColor = TextColor
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen(onError = { viewModel.loadDocuments() })
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onError = { viewModel.loadDocuments() }
            )
            else -> {
                HorizontalPager(
                    state = pagerState,
                    count = filters.size,
                    modifier = Modifier.padding(padding)
                ) { page ->
                    val status = filters[page]
                    DocumentList(
                        documents = state.documents.filter {
                            status == DocumentStatus.ALL || it.status == status
                        },
                        modifier = Modifier.fillMaxSize(),
                        onDocumentClick = onDocumentClick
                    )
                }
            }
        }
    }

    if (state.showCreateDialog) {
        CreateDocumentDialog(
            onCreate = { title, author -> viewModel.createDocument(title, author) },
            onDismiss = { viewModel.toggleCreateDialog(false) }
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FilterRow(
    selectedFilter: DocumentStatus,
    onFilterSelected: (Int) -> Unit,
    pagerState: PagerState
) {
    val filters = DocumentStatus.entries.take(4)
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        scope.launch {
            lazyListState.animateScrollToItem(pagerState.currentPage)
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarColor),
        horizontalArrangement = Arrangement.Center
    ) {
        items(filters) { status ->
            val index = filters.indexOf(status)
            FilterButton(
                status = status,
                isSelected = selectedFilter == status,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    onFilterSelected(index)
                }
            )
        }
    }
}


@Composable
private fun FilterButton(
    status: DocumentStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonAnimationSpec: AnimationSpec<Color> = tween(durationMillis = 300)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = animateColorAsState(
                targetValue = if (isSelected) status.color.copy(alpha = 0.3f) else Color.Transparent,
                animationSpec = buttonAnimationSpec
            ).value,
            contentColor = animateColorAsState(
                targetValue = TextColor,
                animationSpec = buttonAnimationSpec
            ).value
        ),
        border = BorderStroke(
            width = 1.dp,
            color = status.color
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = when (status) {
                DocumentStatus.REJECTED -> "Неподписанные"
                DocumentStatus.IN_PROGRESS -> "Проверка"
                DocumentStatus.SIGNED -> "Подписанные"
                DocumentStatus.ALL -> "Все"
            },
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

@Composable
private fun DocumentList(
    documents: List<Document>,
    modifier: Modifier = Modifier,
    onDocumentClick: (Int) -> Unit,

) {
    LazyColumn(modifier) {
        items(documents) { doc ->
            DocumentCard(
                document = doc,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onDocumentClick(doc.id) }
            )
        }
    }
}

@Composable
private fun DocumentCard(document: Document, modifier: Modifier = Modifier) {
    Card(
        modifier, colors = CardDefaults.cardColors(
            containerColor = CardColor,
            disabledContainerColor = CardColor
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    document.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextColor
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, document.status.color, CircleShape)
                )
            }
            Text(
                "Организация: ${document.organizationId}",
                Modifier.padding(top = 8.dp),
                color = TextColor
            )
            Text(
                "Дата создания: ${document.createdAt}",
                color = TextColor
            )
        }
    }
}

@Composable
private fun CreateDocumentDialog(
    onCreate: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создать документ", color = TextColor) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ButtonColor,
                        unfocusedContainerColor = CardColor,
                        disabledContainerColor = CardColor,
                    )
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Автор") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ButtonColor,
                        unfocusedContainerColor = CardColor,
                        disabledContainerColor = CardColor,
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(title, author) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextColor,
                    containerColor = ButtonColor
                )
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextColor,
                    containerColor = ButtonColor
                )
            ) {
                Text("Отмена")
            }
        },
        containerColor = CardColor
    )
}