package com.example.storyapp.ui.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.nonui.helper.reduceFileImage
import com.example.storyapp.nonui.helper.rotateBitmap
import com.example.storyapp.nonui.helper.uriToFile
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.OverallResponse
import com.example.storyapp.nonui.retrofit.ApiConfig
import com.example.storyapp.ui.liststory.ListActivity
import com.example.storyapp.ui.modelfactory.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddActivity : AppCompatActivity() {
    private lateinit var addActivityBinding: ActivityAddStoryBinding
    private lateinit var addActivityViewModel: AddViewModel
    private var getFile: File? = null
    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addActivityBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addActivityBinding.root)

        setupModel()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        addActivityBinding.cameraButton.setOnClickListener { startCameraX() }
        addActivityBinding.galleryButton.setOnClickListener { startGallery() }
        addActivityBinding.uploadButton.setOnClickListener {
            addActivityViewModel.getUser().observe(this) { UserModel ->
                var user = UserModel(
                    UserModel.name,
                    UserModel.email,
                    UserModel.password,
                    UserModel.userId,
                    UserModel.token,
                    true
                )
                if (user.token.isNotEmpty()) {
                    uploadImage(user.token)
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            addActivityBinding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddActivity)

            getFile = myFile

            addActivityBinding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage(token: String) {

        val userToken = "Bearer $token"
        Log.d(this@AddActivity.toString(), "token = $token")

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = addActivityBinding.tvDescriptionEdittext.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val multiForm = "application/json"

            val service = ApiConfig.getApiService().addStories(imageMultipart, description, userToken, multiForm)

            service.enqueue(object : Callback<OverallResponse> {
                override fun onResponse(
                    call: Call<OverallResponse>,
                    response: Response<OverallResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(this@AddActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            Log.d(this@AddActivity.toString(), responseBody.message)
                            moveactivity()
                        }
                    } else {
                        Toast.makeText(this@AddActivity, response.message(), Toast.LENGTH_SHORT).show()
                        Log.d(this@AddActivity.toString(), response.message())
                    }
                }
                override fun onFailure(call: Call<OverallResponse>, t: Throwable) {
                    Toast.makeText(this@AddActivity, "Retrofit Failed", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@AddActivity, "Input Image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupModel() {
        addActivityViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddViewModel::class.java]

    }

    private fun moveactivity(){
        val listActivityIntent = Intent(this, ListActivity::class.java)
        listActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(listActivityIntent)
        finish()
    }


}
