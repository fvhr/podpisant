package bob.colbaskin.iubip_spring2025.documents.data.remote

import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.DocumentStatus
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentApiService
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DocumentsRepositoryImpl @Inject constructor(
    private val documentApiService: DocumentApiService
) : DocumentsRepository {
    private val mockDocuments = mutableListOf(
        Document(
            id = "1",
            title = "Договор аренды офиса",
            creationDate = "15.05.2024 10:00",
            author = "Иванов И.И.",
            status = DocumentStatus.UNSIGNED
        ),
        Document(
            id = "2",
            title = "Акт выполненных работ №45",
            creationDate = "16.05.2024 14:30",
            author = "Петрова С.К.",
            status = DocumentStatus.VERIFICATION
        )
    )

    override suspend fun getAllDocuments(): List<Document> {
        delay(1000)
        return mockDocuments
    }

    override suspend fun createDocument(title: String, author: String): Document {
        delay(800)
        val newDoc = Document(
            id = (mockDocuments.size + 1).toString(),
            title = title,
            creationDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date()),
            author = author,
            status = DocumentStatus.UNSIGNED
        )
        mockDocuments.add(newDoc)
        return newDoc
    }

    override suspend fun updateDocumentStatus(id: String, status: DocumentStatus) {
        delay(500)
        mockDocuments.replaceAll { if (it.id == id) it.copy(status = status) else it }
    }

    override suspend fun getDocumentById(id: String): Document {
        delay(500)
        return mockDocuments.find { it.id == id }
            ?: throw HttpException(Response.error<String>(404, null))
    }
}