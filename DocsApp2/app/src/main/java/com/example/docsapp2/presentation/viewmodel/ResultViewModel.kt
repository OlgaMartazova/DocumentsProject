package com.example.docsapp2.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docsapp2.data.response.DocResponse
import com.example.docsapp2.domain.usecase.ProcessResultUseCase
import com.example.docsapp2.domain.usecase.UploadFileUseCase
import kotlinx.coroutines.launch
import java.io.File

class ResultViewModel(
    private val uploadFileUseCase: UploadFileUseCase,
    private val processResultUseCase: ProcessResultUseCase,
): ViewModel() {

    private var _result: MutableLiveData<Result<DocResponse>> = MutableLiveData()
    val result: LiveData<Result<DocResponse>> = _result

    fun onUploadFile(file: File) {
        viewModelScope.launch {
            try {
//                uploadFileUseCase(file).also {
//                    _result.value = Result.success(it)
//                }
                Handler(Looper.getMainLooper()).postDelayed({
                    _result.value = Result.success(DocResponse(1, "test", "None", null))
                }, 1000)
            } catch (ex: Exception) {
                _result.value = Result.failure(ex)
            }
        }
    }

    private var _processResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val processResult: LiveData<Result<Boolean>> = _processResult

    fun onProcessResult(resultId: Int, saveResult: Boolean) {
        viewModelScope.launch {
            try {
//                processResultUseCase(resultId, saveResult).also {
//                    _processResult.value = Result.success(it)
//                }
                Handler(Looper.getMainLooper()).postDelayed({
                    _processResult.value = Result.success(true)
                }, 1000)
            } catch (ex: Exception) {
                _processResult.value = Result.failure(ex)
            }
        }
    }
}