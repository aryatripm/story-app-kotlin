package com.arya.submission3.ui.main.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.remote.response.AddStoryResponse
import com.arya.submission3.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    init {
        _isUploaded.value = false
    }

    fun upload(image: MultipartBody.Part, description : RequestBody, latitude : RequestBody?, longitude : RequestBody?, token: String) : LiveData<Result<AddStoryResponse>> = repository.uploadStory(image, description, latitude, longitude, token)

    fun getUserToken(): LiveData<String> {
        return repository.getToken().asLiveData()
    }

    fun setUploaded(state: Boolean) {
        _isUploaded.value = state
    }

    fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    fun setError(msg: String) {
        _errorMessage.value = msg
    }
}