package com.example.docsapp2.domain.usecase

import com.example.docsapp2.domain.repository.DocsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProcessResultUseCase(
    private val repository: DocsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(resultId: Int, saveResult: Boolean): Boolean {
        return withContext(dispatcher) {
            repository.processResult(resultId, saveResult)
        }
    }
}