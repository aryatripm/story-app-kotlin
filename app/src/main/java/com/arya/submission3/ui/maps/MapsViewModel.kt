package com.arya.submission3.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.remote.response.StoriesResponse
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.data.remote.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>()
    val stories: LiveData<ArrayList<Story>> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStories(page: Int, size: Int, token: String) {
        val client = ApiClient.getApiService().getStoriesForMap(page, size,1, "Bearer $token")
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                if (response.isSuccessful) {
                    _stories.value = response.body()?.listStory
                } else {
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _errorMessage.value = t.message
            }
        })
    }

    fun getUserToken(): LiveData<String> {
        return repository.getToken().asLiveData()
    }
}