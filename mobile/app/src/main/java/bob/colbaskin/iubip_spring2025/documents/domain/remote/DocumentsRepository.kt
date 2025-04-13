package bob.colbaskin.iubip_spring2025.documents.domain.remote

import bob.colbaskin.iubip_spring2025.documents.domain.models.Document
import bob.colbaskin.iubip_spring2025.documents.domain.models.Document.DocumentStatus

interface DocumentsRepository {

    suspend fun getAllDocuments(): List<Document>

    suspend fun createDocument(title: String, author: String): Document

    suspend fun updateDocumentStatus(id: Int, status: DocumentStatus)

    suspend fun getDocumentById(id: Int): Document
}