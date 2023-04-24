package com.arya.submission3.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.arya.submission3.data.local.datastore.UserPreferences
import com.arya.submission3.data.local.room.StoryDatabase
import com.arya.submission3.data.remote.response.AddStoryResponse
import com.arya.submission3.data.remote.response.LoginResponse
import com.arya.submission3.data.remote.response.RegisterResponse
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.data.remote.response.StoryResponse
import com.arya.submission3.data.remote.retrofit.ApiService
import com.arya.submission3.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val database: StoryDatabase, private val api: ApiService, private val pref: UserPreferences) {

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading())
        try {
            val response = api.login(email, password)
            pref.saveUserToken(response.loginResult?.token ?: "")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }

    fun register(name:String, email: String, password: String) : LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading())
        try {
            val response = api.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }

    fun getToken() = pref.getUserToken()

    suspend fun saveToken(token: String) = pref.saveUserToken(token)

    suspend fun logout() = pref.deleteUserToken()

    fun getStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, api, pref),
            pagingSourceFactory = {
//                StoryPagingSource(api, pref)
                database.storyDao().getAll()
            }
        ).liveData
    }

    fun uploadStory(image: MultipartBody.Part, description : RequestBody, latitude : RequestBody?, longitude : RequestBody?, token: String) : LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading())
        try {
            val response = if (latitude != null && longitude != null) {
                api.addStory(image, description, latitude, longitude, "Bearer $token")
            } else {
                api.addStory(image, description, "Bearer $token")
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }

    fun getStory(id: String, token: String) : LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading())
        try {
            val response = api.getStory(id, "Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }

}