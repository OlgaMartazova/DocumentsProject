package com.example.docsapp2.data.api

import com.example.docsapp2.data.response.DocResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DocsApi {
    @Multipart
    @POST("analyze_image")
    suspend fun uploadFile(@Part part: MultipartBody.Part): Response<DocResponse>

    @GET("verify_token")
    suspend fun verifyToken(token: String): Response<Boolean>

    @GET("start_new_package")
    suspend fun startPackage(): Response<Void>

    @GET("end_package")
    suspend fun endPackage(): Response<Void>

    @GET("process_result")
    suspend fun processResult(resultId: Int, saveResult: Boolean): Response<Void>
}