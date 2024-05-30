package com.example.docsapp2.domain.repository

import com.example.docsapp2.data.response.DocResponse
import java.io.File

interface DocsRepository {
    suspend fun uploadFile(file: File): DocResponse

    suspend fun verifyToken(token: String): Boolean

    suspend fun startPackage(): Boolean

    suspend fun endPackage(): Boolean

    suspend fun processResult(resultId: Int, saveResult: Boolean): Boolean
}