package com.example.storyapp.ui.login

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
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.nonui.helper.ApiCallbackString
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.ui.addstory.AddActivity
import com.example.storyapp.ui.modelfactory.ViewModelFactory
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.signup.SingupActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)


        activityLoginBinding.btnLogin.setOnClickListener(this)
        activityLoginBinding.textSignin.setOnClickListener(this)

        setupViewModel()

        loginViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        playAnimation()


    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_login){
            val email = activityLoginBinding.theEmailLogin.text.toString()
            val pass = activityLoginBinding.thePasswordLogin.text.toString()

            loginViewModel.login(email, pass, object : ApiCallbackString {
                override fun onResponse(success: Boolean,message: String) {
                    showAlertDialog(success, message)
                }
            })
        }
        if(v.id== R.id.text_signin){
            activityLoginBinding.textSignin.setOnClickListener {
                val moveIntent = Intent(this@LoginActivity, SingupActivity::class.java)
                startActivity(moveIntent)
                finish()
            }
        }

    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }


    private fun showAlertDialog(param: Boolean, message: String) {
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle("Congratulation")
                setMessage("Login Success!")
                setPositiveButton("Continue") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Warning!")
                setMessage("Sign In Failed , $message")
                setPositiveButton("Continue") { _, _ ->
                    activityLoginBinding.progressBar.visibility = View.GONE
                }
                create()
                show()

            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        activityLoginBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(activityLoginBinding.imageView, View.TRANSLATION_X, -30f, 30f)
            .apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
    }

}