package bob.colbaskin.iubip_spring2025.documents.domain.remote

import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus
import retrofit2.http.Path

interface DocumentsRepository {

    suspend fun getAllDocuments(): List<Document>

    suspend fun getDocumentById(id: Int): Document

    suspend fun getDownloadLink(documentId: Int): String

    suspend fun getDepartments(orgId: Int): String

    suspend fun createDocument(title: String, author: String): Document

    suspend fun updateDocumentStatus(id: Int, status: DocumentStatus)
}