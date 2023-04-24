package com.arya.submission3.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.remote.response.LoginResponse
import com.arya.submission3.data.remote.response.RegisterResponse
import com.arya.submission3.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    fun setError(msg: String) {
        _errorMessage.value = msg
    }

    fun getUserToken(): LiveData<String> {
        return repository.getToken().asLiveData()
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = repository.login(email, password)

    fun register(name: String, email: String, password: String) : LiveData<Result<RegisterResponse>> = repository.register(name, email, password)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}