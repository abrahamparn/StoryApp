package com.example.storyapp.ui.liststory

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityListBinding
import com.example.storyapp.nonui.data.LoadingStateAdapter

import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference

import com.example.storyapp.ui.addstory.AddActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.map.MapActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ListActivity : AppCompatActivity(){

    private lateinit var binding: ActivityListBinding
    private lateinit var listViewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupModel()
    }

//    private fun getStory(token: String){
//
//        val userToken = "Bearer $token"
//
//        val client = ApiConfig.getApiService().getAllStories(userToken)
//        client.enqueue(object : Callback<AllStoriesResponse> {
//            override fun onResponse(call: Call<AllStoriesResponse>, response: Response<AllStoriesResponse>) {
//                if(response.isSuccessful){
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        getList(responseBody.listStory)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
//                Log.d(this@ListActivity.toString(), "onError : ${t.message}")
//            }
//        })
//    }
    private fun setupModel() {
        listViewModel = ViewModelProvider(
            this,
            ListViewModel.ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[ListViewModel::class.java]

        listViewModel.getUser().observe(this) { UserModel ->
            var user = UserModel(
                UserModel.name,
                UserModel.email,
                UserModel.password,
                UserModel.userId,
                UserModel.token,
                true
            )

            if (user.token.isNotEmpty()) {
                setStoryData(user.token)
            }
        }
    }

//    private fun getList(stories: List<ListStoryItem>){
//        val listUser = ArrayList<ListModel>()
//        for (story in stories){
//            val storyItem = ListModel(
//                story.name,
//                story.description,
//                story.photoUrl
//            )
//            listUser.add(storyItem)
//        }
//        showRecyclerList(listUser)
//    }
//
//    private fun showRecyclerList(listUser: ArrayList<ListModel>) {
//
//        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            binding.rvUsers.layoutManager = GridLayoutManager(this , 2)
//        }else {
//            binding.rvUsers.layoutManager = LinearLayoutManager(this)
//        }
//
//        val listUserAdapter = ListAdapter(listUser)
//        binding.rvUsers.adapter = listUserAdapter
//    }

    private fun setStoryData(token: String){
        val tokenBearer = "Bearer $token"
        Log.d(this@ListActivity.toString(), tokenBearer)
        val adapter = PagingAdapter()
        binding.rvUsers.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
           binding.rvUsers.layoutManager = GridLayoutManager(this , 2)
        }else {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        }
        listViewModel.getStory(tokenBearer).observe(this) {
            Log.d(this@ListActivity.toString(), it.toString())
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                listViewModel.logout()
                val intentlogout = Intent(this@ListActivity, LoginActivity::class.java)
                startActivity(intentlogout)
                finish()
                return true
            }
            R.id.add_photo -> {
                val intentaddphoto = Intent(this@ListActivity, AddActivity::class.java)
                startActivity(intentaddphoto)
                finish()
                return true
            }
            R.id.the_map ->{
                val intentmap = Intent(this@ListActivity, MapActivity::class.java)
                startActivity(intentmap)
                finish()
                return true
            }
        }
        return true
    }


}