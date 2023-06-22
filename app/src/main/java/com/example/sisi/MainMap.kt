package com.example.sisi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.example.sisi.databinding.ActivityMainMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*


class MainMap() : AppCompatActivity(), OnMapReadyCallback,
    OnRequestPermissionsResultCallback {
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    var needRequest = false

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) // 외부 저장소
    var mCurrentLocatiion: Location? = null
    var currentPosition: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null
    private var mLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLayout = findViewById(R.id.layout_main);
        binding.imageView4.setOnClickListener {
            val intent = Intent(this, Post::class.java)
            startActivity(intent)
        }
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_writing -> {
                    val intent = Intent(applicationContext, WritePost::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_refresh -> {
                    binding.imageView4.setImageResource(R.drawable.img_1)
                    true
                }
                R.id.nav_mypage -> {
                    val intent = Intent(applicationContext, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

}
