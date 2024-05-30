package com.example.docsapp2.domain.usecase

import com.example.docsapp2.domain.repository.TokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignOutUseCase(
    private val repository: TokenRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke() {
        return withContext(dispatcher) {
            repository.deleteToken()
        }
    }
}