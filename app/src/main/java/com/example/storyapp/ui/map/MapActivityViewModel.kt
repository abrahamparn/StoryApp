package com.example.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference

class  MapActivityViewModel(private val pref: UserPreference) : ViewModel(){
    fun getUser() : LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

}