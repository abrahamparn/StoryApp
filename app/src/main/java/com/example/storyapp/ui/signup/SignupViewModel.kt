package com.example.storyapp.ui.signup

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.nonui.helper.ApiCallbackString
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.OverallResponse
import com.example.storyapp.nonui.retrofit.ApiConfig
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignupViewModel(private val preferences: UserPreference): ViewModel() {

    fun getSession(): LiveData<Boolean> {
        return preferences.getSession().asLiveData()
    }
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "RegisterViewModel"
        private const val SUCCESS = "success"
    }


    fun singup(name: String, email: String, pass: String, callback: ApiCallbackString){
        _isLoading.value = true

        val service = ApiConfig.getApiService().register(name, email, pass)
        service.enqueue(object : Callback<OverallResponse> {
            override fun onResponse(call: Call<OverallResponse>, response: Response<OverallResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null && !responseBody.error)
                        callback.onResponse(response.body() != null, SUCCESS)
                    Log.d(this@SignupViewModel.toString(), response.message())

                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    callback.onResponse(false, message)
                }
            }

            override fun onFailure(call: Call<OverallResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure2: ${t.message}")
                callback.onResponse(false, t.message.toString())
            }
        })
    }


}