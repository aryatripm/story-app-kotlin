package com.arya.submission3.ui.main.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.data.remote.response.StoryResponse
import com.arya.submission3.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    fun getStory(id: String, token: String) : LiveData<Result<StoryResponse>> = repository.getStory(id, token)

    fun getUserToken(): String {
        var token = ""
        viewModelScope.launch {
            token = repository.getToken().first()
        }
        return token
    }

    fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    fun setError(msg: String) {
        _errorMessage.value = msg
    }
}