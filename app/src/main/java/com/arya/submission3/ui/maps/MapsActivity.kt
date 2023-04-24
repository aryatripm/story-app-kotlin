package com.arya.submission3.ui.maps

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arya.submission3.R
import com.arya.submission3.databinding.ActivityMapsBinding
import com.arya.submission3.utils.ViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel : MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.title = "Story with Map"
        binding.toolbar.isTitleCentered = true

        viewModel.errorMessage.observe(this) {
            showSnackbar(it)
        }

        viewModel.getUserToken().observe(this) {
            viewModel.getStories(1, 10, it)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setMaxZoomPreference(10f)

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        viewModel.stories.observe(this) {

            val latLngBoundBuilder = LatLngBounds.Builder()

            it.forEach { story ->
                val latLng = LatLng(story.lat!!, story.lon!!)

                Glide.with(this)
                    .asBitmap()
                    .load(story.photoUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            mMap.addMarker(MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resource, 100, 100, true)))
                            )
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            showSnackbar("Image load error")
                        }
                    })
                latLngBoundBuilder.include(latLng)
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundBuilder.build(), resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, 300))
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
}