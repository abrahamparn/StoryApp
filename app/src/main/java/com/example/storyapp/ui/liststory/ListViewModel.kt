package com.example.storyapp.ui.liststory

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.nonui.data.Injection
import com.example.storyapp.nonui.data.StoriesRepository
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.AllStoriesResponse
import com.example.storyapp.nonui.response.ListStoryItem
import com.example.storyapp.nonui.retrofit.ApiConfig
import com.example.storyapp.ui.modelfactory.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListViewModel(private val pref: UserPreference, private val storiesRepository: StoriesRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }


    fun getUser() : LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStory(token : String): LiveData<PagingData<ListStoryItem>> =
        storiesRepository.getStoriesForPaging(token).cachedIn(viewModelScope)

    class ViewModelFactory(private val preferences: UserPreference, private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ListViewModel(preferences, Injection.provideRepository(context)) as T
            }
            else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}