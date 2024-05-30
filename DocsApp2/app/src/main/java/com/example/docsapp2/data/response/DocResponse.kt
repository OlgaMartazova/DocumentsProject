package com.example.docsapp2.data.response

data class DocResponse (
    val id: Int = 0,
    val fileName: String,
    val docType: String = "None",
    val result: Result? = null
)