package com.example.docsapp2.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docsapp2.domain.usecase.VerifyTokenUseCase
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper

class SignInViewModel(
    private val verifyTokenUseCase: VerifyTokenUseCase
): ViewModel() {

    private var _result: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val result: LiveData<Result<Boolean>> = _result

    fun onVerifyToken(token: String) {
        viewModelScope.launch {
            try {
//                verifyTokenUseCase(token).also {
//                    _result.value = Result.success(it)
//                }
                Handler(Looper.getMainLooper()).postDelayed({
                    _result.value = Result.success(true)
                }, 2000)
            } catch (ex: Exception) {
                _result.value = Result.failure(ex)
            }
        }
    }
}