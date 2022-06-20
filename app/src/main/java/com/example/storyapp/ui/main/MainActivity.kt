package com.example.storyapp.ui.main

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.ui.addstory.AddActivity
import com.example.storyapp.ui.liststory.ListActivity
import com.example.storyapp.ui.modelfactory.ViewModelFactory
import com.example.storyapp.ui.login.LoginActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var actvityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actvityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(actvityMainBinding.root)

        setupViewModel()
        actvityMainBinding.btnLogout.setOnClickListener(this)
        actvityMainBinding.btnContinue.setOnClickListener(this)

        playAnimation()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { UserModel ->
            var user = UserModel(
                UserModel.name,
                UserModel.email,
                UserModel.password,
                UserModel.userId,
                UserModel.token,
                true
            )
            actvityMainBinding.text1Activitymain.text = "Hi ${UserModel.name}"
        }
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_logout){
            mainViewModel.logout()
            showAlertDialog()
        }
        if(v.id == R.id.btn_continue){
            val moveIntent = Intent(this@MainActivity, ListActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
    }

    private fun showAlertDialog(){
        AlertDialog.Builder(this).apply {
            setTitle("I am Sad")
            setMessage("Log out success :(")
            setPositiveButton("Continue") { _, _ ->
                val moveIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(moveIntent)
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(actvityMainBinding.imageView, View.TRANSLATION_X, -30f, 30f)
            .apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
    }
}
