package bob.colbaskin.iubip_spring2025.documents.domain.remote

import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.DocumentStatus

interface DocumentsRepository {

    suspend fun getAllDocuments(): List<Document>

    suspend fun createDocument(title: String, author: String): Document

    suspend fun updateDocumentStatus(id: String, status: DocumentStatus)

    suspend fun getDocumentById(id: String): Document
}