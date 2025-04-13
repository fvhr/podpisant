package bob.colbaskin.iubip_spring2025.documents.domain.remote

import bob.colbaskin.iubip_spring2025.documents.data.models.DocumentDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface DocumentApiService {

    @GET("documents")
    suspend fun getAllDocuments(): List<DocumentDTO>

    @GET("documents/{document_id}")
    suspend fun getDocumentById(@Path("document_id") documentId: Int): DocumentDTO

    @GET("documents/{document_id}/file")
    suspend fun getDownloadLink(@Path("document_id") documentId: Int): String
}