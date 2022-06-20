package com.example.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySingupBinding
import com.example.storyapp.nonui.helper.ApiCallbackString
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.ui.addstory.AddActivity
import com.example.storyapp.ui.liststory.ListActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.login.LoginViewModel
import com.example.storyapp.ui.main.MainViewModel
import com.example.storyapp.ui.modelfactory.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SingupActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var activitySignupBinding: ActivitySingupBinding

    private lateinit var singupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignupBinding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(activitySignupBinding.root)

        activitySignupBinding.btnSignup.setOnClickListener(this)
        activitySignupBinding.textLogin.setOnClickListener(this)

        setupView()
        playAnimation()
        setupModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun showAlertDialog(param: Boolean, message: String) {
        if (param) {
            AlertDialog.Builder(this).apply {
                setTitle("Congratulation")
                setMessage("Register success, you can sign in wih you account")
                setPositiveButton("Continue") { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Warning")
                setMessage("Sign up Failed , $message")
                setPositiveButton("Continue") { _, _ ->
                    activitySignupBinding.progressBar.visibility = View.GONE
                }
                create()
                show()
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_signup) {
            val name = activitySignupBinding.theNameSignup.text.toString()
            val email = activitySignupBinding.theEmailSignup.text.toString()
            val password = activitySignupBinding.thePasswordSignup.text.toString()

            singupViewModel.singup(name, email, password, object : ApiCallbackString {
                override fun onResponse(success: Boolean, message: String) {
                    showAlertDialog(success, message)
                }
            })
        }
        if (v.id == R.id.text_login) {
            val moveIntent = Intent(this@SingupActivity, LoginActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(activitySignupBinding.imageView, View.TRANSLATION_X, -30f, 30f)
            .apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
    }

    private fun setupModel(){
        singupViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[SignupViewModel::class.java]

        singupViewModel.getSession().observe(this) {session ->
            if(session){
                val storyIntent = Intent(this, ListActivity::class.java)
                startActivity(storyIntent)
                finish()
            }
        }
    }
}