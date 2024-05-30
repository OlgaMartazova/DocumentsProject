package com.example.docsapp2.data.mapper

import android.util.Log
import com.example.docsapp2.data.response.DocResponse
import retrofit2.Response

class Mapper {
    fun mapPassportResult(response: Response<DocResponse>): DocResponse {
        return if (response.isSuccessful) {
            Log.e("response", response.body()?.result?.toString() ?: "null")
            requireNotNull(response.body())
        }
        else DocResponse(fileName = "None", docType = "None")
    }
}