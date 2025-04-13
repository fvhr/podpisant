package bob.colbaskin.iubip_spring2025.documents.domain.remote

import bob.colbaskin.iubip_spring2025.documents.data.models.DocumentDTO
import retrofit2.http.GET

interface DocumentApiService {

    @GET("documents")
    suspend fun getAllDocuments(): List<DocumentDTO>
}