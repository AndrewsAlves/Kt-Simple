package com.task.ktsimple.view

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.task.ktsimple.R
import com.task.ktsimple.adapters.RecyclerViewAdapter
import com.task.ktsimple.adapters.RecyclerViewAdapterLocation
import com.task.ktsimple.adapters.SpinnerAdapter
import com.task.ktsimple.databinding.ActivityLocationsListBinding
import com.task.ktsimple.interfaces.ItemClickedListener
import com.task.ktsimple.model.User
import com.task.ktsimple.viewmodel.LocationListViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val TAG = "LocationsListActivity"

class LocationsListActivity : AppCompatActivity(), OnItemSelectedListener{

    private val viewModel: LocationListViewModel by viewModels()
    private lateinit var ui: ActivityLocationsListBinding
    private val PERMISION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLocationsListBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.btnIbBack.setOnClickListener {
            finish()
        }

        // Initilise Adapter
        Log.d("Locotion List", "length ${viewModel.signedInUserList.value!!.size}")
        val spAdapter = SpinnerAdapter(this, R.layout.item_spinner, viewModel.signedInUserList.value!!)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ui.spUserSpinner.adapter = spAdapter
        ui.spUserSpinner.onItemSelectedListener = this
        ui.spUserSpinner.setSelection(viewModel.getUserIndex(viewModel.currentUser.value!!))

        updateLocationText(viewModel.currentUser.value!!.userName)

        viewModel.locationPermission.observe(this) {
            if (it) {
                // Start Location Service
                viewModel.startLocationService(applicationContext)
            } else {
                Snackbar.make(ui.root, "Need Location Permission granted All time to use this activity", Snackbar.LENGTH_LONG).show()
            }
        }

        // Recycler View
        val clickedListener = object : ItemClickedListener {
            override fun itemClicked(index: Int, item: Any) {
                startActivity(Intent(this@LocationsListActivity, MapsActivity::class.java))
            }
        }

        ui.rvLocations.layoutManager = LinearLayoutManager(this)
        val rvAdapter = RecyclerViewAdapterLocation(this, viewModel.currentUser.value!!.locations)
        ui.rvLocations.adapter = rvAdapter
        rvAdapter.setClickedListener(clickedListener)

        viewModel.currentUser.observe(this) {
            updateLocationText(it.userName)
            rvAdapter.dataList = viewModel.currentUser.value!!.locations
            rvAdapter.notifyDataSetChanged()
        }

        // Start With location
        handleLocationPermission()
        updateLocationRvThread()
    }

    fun updateLocationRvThread() {
        lifecycleScope.launch {
            while (true) {
                viewModel.updateUserLocations()
                delay(10100)
            }
        }
    }

    fun updateLocationText(userName : String) {
        ui.tvNameLocations.text = userName + "'s locations" + " (" + viewModel.currentUser.value!!.locations.size + ")"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedUser = parent?.getItemAtPosition(position) as User
        viewModel.updateCurrentUser(selectedUser)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}


    /**
     *
     * HANDLE PERMISSIONS
     *
     */

    fun handleLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!isGPSEnabled(this)) showEnableGPSDialog(this)
            viewModel.updateLocationPermission(true)
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISION_REQUEST_CODE)
        }
    }

    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.updateLocationPermission(true)
                if (!isGPSEnabled(this)) showEnableGPSDialog(this)
                viewModel.startLocationService(applicationContext)

            } else {
                viewModel.updateLocationPermission(false)
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     *
     * HANDLE USER TO TURN ON THE LOCATION
     *
     */

    fun showEnableGPSDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Enable GPS")
            setMessage("GPS is required for Higher Accuracy. Turn it ON in Settings. Go to Settings?")
            setCancelable(false)
            setPositiveButton("Yes") { _, _ ->
                // Open location settings
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /**
     *
     * CHECK SERVICE IS ALREADY RUNNING
     */

    fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.task.ktsimple" == service.service.className) {
                return true
            }
        }
        return false
    }

    /*class CountryAdapter(
        context: Context
    ) : ArrayAdapter<User>(context, 0, OperatedCountry.values()) {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.item_country, parent, false)
            } else {
                view = convertView
            }
            getItem(position)?.let { country ->
                setItemForCountry(view, country)
            }
            return view
        }
        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view: View
            if (position == 0) {
                view = layoutInflater.inflate(R.layout.header_country, parent, false)
                view.setOnClickListener {
                    val root = parent.rootView
                    root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                    root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
                }
            } else {
                view = layoutInflater.inflate(R.layout.item_country_dropdown, parent, false)
                getItem(position)?.let { country ->
                    setItemForCountry(view, country)
                }
            }
            return view
        }

        override fun getItem(position: Int): OperatedCountry? {
            if (position == 0) {
                return null
            }
            return super.getItem(position - 1)
        }

        override fun getCount() = super.getCount() + 1
        override fun isEnabled(position: Int) = position != 0
        private fun setItemForCountry(view: View, country: OperatedCountry) {
            val tvCountry = view.findViewById<TextView>(R.id.tvCountry)
            val ivCountry = view.findViewById<ImageView>(R.id.ivCountry)
            val countryName = Locale("", country.countryCode).displayCountry
            tvCountry.text = countryName
            ivCountry.setBackgroundResource(country.icon)
        }
    }*/
}