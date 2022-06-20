package com.example.storyapp.nonui.data

import android.content.Context
import com.example.storyapp.nonui.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoriesRepository(database, apiService)
    }
}