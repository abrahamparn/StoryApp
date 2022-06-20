package com.example.storyapp.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapBinding

import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.nonui.model.UserPreference
import com.example.storyapp.nonui.response.AllStoriesResponse
import com.example.storyapp.nonui.response.ListStoryItem
import com.example.storyapp.nonui.retrofit.ApiConfig
import com.example.storyapp.ui.modelfactory.ViewModelFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapActivityViewModel: MapActivityViewModel
    private lateinit var token: String


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupModel()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupModel() {
        mapActivityViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MapActivityViewModel::class.java]

        mapActivityViewModel.getUser().observe(this) { UserModel ->
            var user = UserModel(
                UserModel.name,
                UserModel.email,
                UserModel.password,
                UserModel.userId,
                UserModel.token,
                true
            )

            if (user.token.isNotEmpty()) {
                token = user.token
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true




        val userToken = "Bearer $token"

        val client = ApiConfig.getApiService().AllStoriesResponse(userToken)
        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(call: Call<AllStoriesResponse>, response: Response<AllStoriesResponse>) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null) {
                        var the_stories: List<ListStoryItem>
                        the_stories = responseBody.listStory
                        for (story in the_stories) {
                            val position = LatLng(story.lat, story.lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(story.name)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                Log.d(this@MapActivity.toString(), "onError : ${t.message}")
            }
        })


    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}