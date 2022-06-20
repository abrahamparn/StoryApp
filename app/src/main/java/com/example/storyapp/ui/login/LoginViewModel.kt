package com.example.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.nonui.helper.ApiCallbackString
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.LoginResponse
import com.example.storyapp.nonui.retrofit.ApiConfig
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference):ViewModel() {

    companion object {
        private const val TAG = "SignInViewModel"
        private const val SUCCESS = "success"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(email: String, pass: String, callback: ApiCallbackString){
        _isLoading.value = true

        val service = ApiConfig.getApiService().login(email, pass)
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {

                        callback.onResponse(response.body() != null, SUCCESS)

                        val User = UserModel(
                            responseBody.loginResult.name,
                            email,
                            pass,
                            responseBody.loginResult.userId,
                            responseBody.loginResult.token,
                            true
                        )
                        saveUser(User)
                    }
                    Log.d(this@LoginViewModel.toString(), response.message())

                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")

                    // get message error
                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    callback.onResponse(false, message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                callback.onResponse(false, t.message.toString())
            }
        })
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

}