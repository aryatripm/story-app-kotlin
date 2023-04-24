package com.arya.submission3.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arya.submission3.data.StoryRepository
import com.arya.submission3.data.local.datastore.UserPreferences
import com.arya.submission3.data.local.room.StoryDatabase
import com.arya.submission3.data.remote.retrofit.ApiClient

object Injection  {
    fun provideRepository(context: Context, dataStore: DataStore<Preferences>): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val api = ApiClient.getApiService()
        val pref = UserPreferences.getInstance(dataStore)
        return StoryRepository(database, api, pref)
    }
}