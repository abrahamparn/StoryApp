package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.storyapp.nonui.data.StoriesRepository
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel()  {


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}