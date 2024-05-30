package com.example.docsapp2.domain.usecase

import com.example.docsapp2.data.response.DocResponse
import com.example.docsapp2.domain.repository.DocsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UploadFileUseCase(
    private val repository: DocsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(file: File): DocResponse {
        return withContext(dispatcher) {
            repository.uploadFile(file)
        }
    }
}