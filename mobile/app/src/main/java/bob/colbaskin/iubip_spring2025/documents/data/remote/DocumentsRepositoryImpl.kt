package bob.colbaskin.iubip_spring2025.documents.data.remote

import bob.colbaskin.iubip_spring2025.documents.data.models.toDomain
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentType
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentApiService
import bob.colbaskin.iubip_spring2025.documents.domain.remote.DocumentsRepository
import javax.inject.Inject

class DocumentsRepositoryImpl @Inject constructor(
    private val documentApiService: DocumentApiService
) : DocumentsRepository {

    override suspend fun getAllDocuments(): List<Document> {
        return documentApiService.getAllDocuments().map { it.toDomain() }
    }

    override suspend fun createDocument(title: String, author: String): Document {
        return Document(
            id = 2331,
            name = "name 1",
            organizationId = 1,
            fileUrl = "asd",
            createdAt = "asd",
            status = DocumentStatus.SIGNED,
            type = DocumentType.STRICT,
            creatorId = ""
        )
    }

    override suspend fun updateDocumentStatus(id: Int, status: DocumentStatus) {

    }

    override suspend fun getDocumentById(id: Int): Document {
        return Document(
            id = 12345,
            name = "name 1",
            organizationId = 1,
            fileUrl = "asd",
            createdAt = "asd",
            status = DocumentStatus.SIGNED,
            type = DocumentType.STRICT,
            creatorId = ""
        )
    }
}