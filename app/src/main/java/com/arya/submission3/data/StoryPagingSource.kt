package com.arya.submission3.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arya.submission3.data.local.datastore.UserPreferences
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiService: ApiService, private val pref: UserPreferences) : PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(position, params.loadSize, 0, "Bearer ${pref.getUserToken().first()}")
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}