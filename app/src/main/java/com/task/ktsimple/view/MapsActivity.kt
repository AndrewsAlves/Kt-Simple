package com.task.ktsimple.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.task.ktsimple.R
import com.task.ktsimple.databinding.ActivityMapsBinding
import com.task.ktsimple.viewmodel.MapsActivityViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var ui: ActivityMapsBinding

    var animationJob : Job? = null
    var marker : Marker? = null

    private val viewModel : MapsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(ui.root)


        ui.llPlay.visibility = View.INVISIBLE

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        ui.llPlay.visibility = View.VISIBLE

        val location = viewModel.getInitialLocation()
        if (location != null) {
            ui.tvLocationDetails.text = location.address
        } else {
            ui.tvLocationDetails.text = "No User Locations"
            ui.ivPlayLocations.setOnClickListener(null)
        }

        val userLoc = LatLng(21.7679, 78.8718)
        marker = mMap.addMarker(MarkerOptions().position(userLoc))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc))

        setObservers()
        viewModel.updateAnimatingIndex(intent.getIntExtra("location_index", 0))
    }

    fun setMapsLoc() {

        val location = viewModel.animatingLocation.value!!

        ui.tvLocationDetails.text = location.address

        val userLoc = LatLng(location.lat, location.lon)
        marker!!.position = userLoc
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15f))
    }

    fun setObservers() {
        viewModel.playingLocation.observe(this) {

            if (it) {
                 animationJob = lifecycleScope.launch(Dispatchers.IO) {
                    while (viewModel.animatingIndex.value!! < viewModel.totalLocation - 1 && this.isActive) {
                        delay(3000)
                        withContext(Dispatchers.Main) {
                            viewModel.incrementAnimatingIndex()
                        }
                    }

                     withContext(Dispatchers.Main) {
                         ui.ivPlayLocations.performClick()
                     }
                }
            } else {
                animationJob?.cancel()
            }
        }

        viewModel.animatingLocation.observe(this) {
            setMapsLoc()
        }

        ui.ibBack.setOnClickListener {
            finish()
        }

        ui.ivPlayLocations.setOnClickListener {
            Log.d(TAG, "Performed Click")
            viewModel.playOrPause()
            if (viewModel.playingLocation.value!!) ui.ivPlayLocations.setImageDrawable(getDrawable(R.drawable.ic_pause))
            else ui.ivPlayLocations.setImageDrawable(getDrawable(R.drawable.ic_play))
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


}