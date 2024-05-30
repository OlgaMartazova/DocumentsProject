package com.example.docsapp2.domain.usecase

import com.example.docsapp2.domain.repository.DocsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyTokenUseCase(
    private val repository: DocsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(token: String): Boolean {
        return withContext(dispatcher) {
            repository.verifyToken(token)
        }
    }
}