package com.example.speedometer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import br.com.helpdev.velocimetroalerta.gps.Gps
import com.example.Helper.provideHistoryAddTaskViewModelFactory
import com.example.HistoryDatabase.HistoryEntityDataClass
import com.example.Utils.GpsUtils.calAvgSpeed
import com.example.Utils.GpsUtils.calculateDistances
import com.example.Utils.Utility
import com.example.ViewModels.ItemViewModel
import com.example.speedometer.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var currentDateString: String
    private lateinit var modelClass: HistoryEntityDataClass
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var historyViewModel: HistoryAddTaskViewModel

    private val mInterval = 1000L
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var lat: Double? = null
    private var lng: Double? = null
    private var MaxSpeed: Int = 0
    private val LOCATION_PERM = 124
    private var isClicked: Boolean? = null
    private val gps = Gps()
    private var incomingLocation: Location? = null
    private var timecalc: String? = null
    private var distance: Double = 0.0
    private var avgSpeed: Int = 0
    private var accSpeed: Int = 0
    private var acc: Int = 0
    private var speedtoInt by Delegates.notNull<Float>()
    private var locationCallback: LocationCallback? = null
    private val viewModel: ItemViewModel by viewModels()

    private var isDone: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        if (newValue == true) {
            locationCallback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationPermissionRequest()
        createLocationRequest()

        historyViewModel = ViewModelProvider(
            this,
            provideHistoryAddTaskViewModelFactory()
        ).get(HistoryAddTaskViewModel::class.java)


        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        var viewPager = findViewById<ViewPager>(R.id.viewPager1);
        viewPagerAdapter = ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5)
        var tabLayout = findViewById<TabLayout>(R.id.tabLayput);
        tabLayout.setupWithViewPager(viewPager);


        viewModel.selectedItem.observe(this, androidx.lifecycle.Observer { buttonState ->
            if (buttonState == false) {
                if (incomingLocation != null) {
                    historyViewModel.insertHistory(
                        HistoryEntityDataClass(
                            0,
                            startLocationName(),
                            endLocationName(),
                            currentDateString,
                            distance!!,
                            timecalc.toString(),
                            avgSpeed!!
                        )
                    )
                }
                stopTimer()
                closeLocationUpdate()
                isClicked = buttonState
            } else {
                isClicked = buttonState
                startTimer()
                start()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                }, 1000)

            }
        })
        // Drawer Layout
        layout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, layout, R.string.open, R.string.close)
        layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myPremium -> Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT)
                    .show()
                R.id.myHistory -> {
                    val intent = Intent(this, HistoryRecordActivity::class.java)
                    startActivity(intent)
                }
                R.id.mySettings -> Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT)
                    .show()
                R.id.myPrivacy -> Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT)
                    .show()
                R.id.myShare -> Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT)
                    .show()
            }
            layout.closeDrawer(GravityCompat.START)
            true
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
        timeInSeconds = 0L
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                Log.e("timeInSeconds", timeInSeconds.toString())
                updateStopWatchView(timeInSeconds)
            } finally {
                mHandler!!.postDelayed(this, mInterval)
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = Utility.getFormattedStopWatch((timeInSeconds * 250))
        Log.e("formattedTime", formattedTime)
        timecalc = formattedTime
        viewModel.setTime(timecalc!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationCallback?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }

    fun closeLocationUpdate() {
        locationCallback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
        gps.init(this)
        gps.close()
        lat = null
        lng = null
        accSpeed = 0
        MaxSpeed = 0
        distance = 0.00
        avgSpeed = 0
        incomingLocation = null
        locationCallback = null
        modelClass = HistoryEntityDataClass(
            0,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            0.00,
            0,
            0,
            null,
        )
        sendingLocationwithData(modelClass)
    }

    private fun start() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                if (!isDone) {
                    acc = locationResult.lastLocation!!.accuracy.roundToInt()
                    speedtoInt = locationResult.lastLocation!!.speed
                    accSpeed = (speedtoInt * 3.6f).roundToInt()
                    if (accSpeed != 0) {
                        if (MaxSpeed < accSpeed) {
                            MaxSpeed = accSpeed
                        }
                    }
                }
                gps.init(this@MainActivity)
                incomingLocation = gps.getLocation()
                if (incomingLocation != null && isClicked == true) {
                    if (lat == null && lng == null) {
                        lat = incomingLocation!!.latitude
                        lng = incomingLocation!!.longitude
                    }
                    distance = calculateDistances(incomingLocation!!)
                    avgSpeed = toDecimalString(calAvgSpeed()).toInt()
                    modelClass = HistoryEntityDataClass(
                        0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        accSpeed,
                        MaxSpeed,
                        distance,
                        avgSpeed,
                        acc,
                        null,
                        incomingLocation!!.latitude,
                        incomingLocation!!.longitude
                    )
                    sendingLocationwithData(modelClass)
                }
            }
        }
        startLocationUpdate()
    }

    fun startLocationName(): String {
        var locality = ""
        val gcd = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = lng?.let { lat?.let { it1 -> gcd.getFromLocation(it1, it, 1) } }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addresses != null && addresses.size > 0) {
            locality = addresses[0].getAddressLine(0)
        }
        return locality
    }

    fun endLocationName(): String {
        var locality = ""
        val gcd = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses =
                gcd.getFromLocation(incomingLocation!!.latitude, incomingLocation!!.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addresses != null && addresses.size > 0) {
            locality = addresses[0].getAddressLine(0)

        }
        currentDateString = DateFormat.getDateInstance().format(Date())
        return locality
    }

    fun toDecimalString(value: Double): String = "${value.toInt()}"

    fun sendingLocationwithData(item: HistoryEntityDataClass) {
        viewModel.cominglocation(item)
    }

    private fun locationPermissionRequest() {
        if (hasLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "",
                LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}