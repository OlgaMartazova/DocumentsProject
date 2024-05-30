package com.example.docsapp2.data.impl

import com.example.docsapp2.data.api.DocsApi
import com.example.docsapp2.data.mapper.Mapper
import com.example.docsapp2.data.response.DocResponse
import com.example.docsapp2.domain.repository.DocsRepository
import com.example.docsapp2.domain.repository.TokenRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DocsRepositoryImpl(
    private val api: DocsApi,
    private val mapper: Mapper,
    private val tokenRepository: TokenRepository,
) : DocsRepository {

    override suspend fun uploadFile(file: File): DocResponse {
        val part = MultipartBody.Part.createFormData(
            "image", file.name, file.asRequestBody()
        )
        return mapper.mapPassportResult(api.uploadFile(part))
    }

    override suspend fun verifyToken(token: String): Boolean {
        val response = api.verifyToken(token)
        return if (response.isSuccessful) {
            response.body()?.let { verified ->
                if (verified) {
                    tokenRepository.saveToken(token)
                }
            }
            response.body() ?: false
        } else false
    }

    override suspend fun startPackage() = api.startPackage().isSuccessful

    override suspend fun endPackage() = api.endPackage().isSuccessful

    override suspend fun processResult(resultId: Int, saveResult: Boolean) = api.processResult(resultId, saveResult).isSuccessful
}