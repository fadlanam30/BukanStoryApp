package com.example.mystoryapp.data

import androidx.activity.viewModels
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystoryapp.ListStoryItem
import com.example.mystoryapp.api.ApiService
import com.example.mystoryapp.model.AccountPreferences
import com.example.mystoryapp.preferences.PrefViewModel
import com.example.mystoryapp.preferences.ViewModelFactory
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StoryPagingSource @Inject constructor (private val apiService: ApiService, private val preferences: AccountPreferences) : PagingSource<Int, ListStoryItem>() {



    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {

            val token = preferences.getInfo().first().token

            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseStory = apiService.getAllStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseStory.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseStory.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}