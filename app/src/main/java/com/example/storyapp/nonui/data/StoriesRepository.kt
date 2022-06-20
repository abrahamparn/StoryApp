package com.example.storyapp.nonui.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.nonui.response.ListStoryItem
import com.example.storyapp.nonui.retrofit.ApiService

class StoriesRepository(private val storiesDatabase: StoriesDatabase, private val apiService: ApiService) {
    fun getStoriesForPaging(headerdata: String) : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                paginglist(apiService, headerdata)
            }
        ).liveData
    }
}
