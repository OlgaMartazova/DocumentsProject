package com.example.docsapp2.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docsapp2.domain.usecase.SignOutUseCase
import com.example.docsapp2.domain.usecase.StartPackageUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val startPackageUseCase: StartPackageUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    fun onSignOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    private var _result: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val result: LiveData<Result<Boolean>> = _result

    fun onStartPackage() {
        viewModelScope.launch {
            try {
//                startPackageUseCase().also {
//                    _result.value = Result.success(it)
//                }
                Handler(Looper.getMainLooper()).postDelayed({
                    _result.value = Result.success(true)
                }, 1000)
            } catch (ex: Exception) {
                _result.value = Result.failure(ex)
            }
        }
    }
}