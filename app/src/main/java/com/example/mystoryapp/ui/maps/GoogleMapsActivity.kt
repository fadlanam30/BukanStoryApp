package com.example.mystoryapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.databinding.ActivityGoogleMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGMap: GoogleMap

    private val gMapsViewModel by viewModels<GMapsViewModel>()

    private var _mapsBinding: ActivityGoogleMapsBinding? = null
    private val binding get() = _mapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mapsBinding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGMap = googleMap

        getMyLocation()

        showLocationMarker()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLocationMarker() {
        gMapsViewModel.getStoryLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        val data = result.data
                        for (i in data){
                            val locationStory = LatLng(i.lat, i.lon)
                            mGMap.addMarker(
                                MarkerOptions()
                                    .position(locationStory)
                                    .title(i.name)
                                    .snippet(i.description)
                            )
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _mapsBinding = null
    }

}