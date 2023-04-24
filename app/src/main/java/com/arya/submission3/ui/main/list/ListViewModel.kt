package com.arya.submission3.ui.main.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.remote.response.Story

class ListViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun setLoading(state: Boolean) {
        _isLoading.value = state
    }

    val stories: LiveData<PagingData<Story>> = storyRepository.getStories().cachedIn(viewModelScope)

}