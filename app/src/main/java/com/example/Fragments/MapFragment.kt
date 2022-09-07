package com.example.Fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.Utils.GpsUtils
import com.example.ViewModels.ItemViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: ItemViewModel by activityViewModels()

    var map: GoogleMap? = null
    private lateinit var mapView: MapView
    private lateinit var mapTimeView: TextView
    private lateinit var startPauseIcon: ImageView
    private lateinit var startTrip: TextView
    private lateinit var startButton: View
    private lateinit var mapAvgDigit: TextView
    private lateinit var mapDistanceDigit: TextView
    private lateinit var mapMaxDigit: TextView
    private lateinit var accuracyDigit: TextView
    private var mLastClickTime: Long = 0
    var setCurrentcatin=1

    private var isClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.example.speedometer.R.layout.fragment_map, container, false)

        mapTimeView = view.findViewById(com.example.speedometer.R.id.time)
        startPauseIcon = view.findViewById(com.example.speedometer.R.id.playPauseIcon)
        startTrip = view.findViewById(com.example.speedometer.R.id.mapStartTrip)
        startButton = view.findViewById(com.example.speedometer.R.id.mapStart_btn)
        mapAvgDigit = view.findViewById(com.example.speedometer.R.id.mapAvgSpeedDigit)
        mapDistanceDigit = view.findViewById(com.example.speedometer.R.id.mapDistanceDigit)
        mapMaxDigit = view.findViewById(com.example.speedometer.R.id.mapMaxSpeedDigit)
        accuracyDigit = view.findViewById(com.example.speedometer.R.id.mapAccuracyDigit)
        mapView = view.findViewById(com.example.speedometer.R.id.mapView)


        viewModel.selectedState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it
            if (it == false) {
                isClicked = false
                start()
                startPauseIcon.setImageResource(com.example.speedometer.R.drawable.ic_baseline_play_arrow_24)
                startButton.setBackgroundResource(com.example.speedometer.R.drawable.button_background)
                startTrip.setText("Start Trip")
            } else {
                isClicked = true
                startTrip.setText("Stop Trip")
                startPauseIcon.setImageResource(com.example.speedometer.R.drawable.ic_baseline_pause_24)
                startButton.setBackgroundResource(com.example.speedometer.R.drawable.stopbutton_background)
                onItemClicked(it)
            }
        })
        start()

        startButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return@setOnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isClicked) {
                startPauseIcon.setImageResource(com.example.speedometer.R.drawable.ic_baseline_play_arrow_24)
                startButton.setBackgroundResource(com.example.speedometer.R.drawable.button_background)
                startTrip.setText("Start Trip")
                isClicked = false
                onItemClicked(isClicked)
                onButtonStateChange(isClicked)
                GpsUtils.locationCheck(null)
            } else {
                isClicked = true
                onItemClicked(isClicked)
                onButtonStateChange(isClicked)
                start()
                startTrip.setText("Stop Trip")
                startPauseIcon.setImageResource(com.example.speedometer.R.drawable.ic_baseline_pause_24)
                startButton.setBackgroundResource(com.example.speedometer.R.drawable.stopbutton_background)
                onItemClicked(isClicked)
            }
        }

        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

        return view

    }

    private fun start() {
        viewModel.timeObserver.observe(viewLifecycleOwner, Observer {
            mapTimeView.text = it
        })
        viewModel.comingItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            if (isClicked == true) {
                if (it != null) {
                    mapDistanceDigit.text = it.travelDistance.toString()
                    mapMaxDigit.text = it.MaxSpeed.toString()
                    mapAvgDigit.text = it.averageSpeed.toString()
                    accuracyDigit.text = it.accuracy.toString()
                    var zoomLevel = 16.0f
                    map!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.lat, it.log),
                            zoomLevel
                        )
                    )
                }
            } else {
                mapTimeView.text = "00:00:00"
                mapDistanceDigit.text = "0.00"
                mapMaxDigit.text = "0"
                mapAvgDigit.text = "0"
                accuracyDigit.text = "0"
            }
        })
    }

    fun onItemClicked(item: Boolean) {
        viewModel.fragmentToActivityButtonState(item)
    }


    fun onButtonStateChange(item: Boolean) {
        viewModel.FtoFButtonState(item)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        if (mapView.getMapAsync(this) != null) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }
            map!!.isMyLocationEnabled = true
            map!!.uiSettings.isMyLocationButtonEnabled = true;




                map!!.setOnMyLocationChangeListener(GoogleMap.OnMyLocationChangeListener() {
                    var newCamPos = CameraPosition(
                        LatLng(it.latitude, it.longitude),
                        16F,
                        map!!.cameraPosition.tilt,
                        map!!.cameraPosition.bearing
                    )
                    if (setCurrentcatin==1){
                        map!!.moveCamera(CameraUpdateFactory.newCameraPosition(newCamPos))
                    }
                    setCurrentcatin=2
                })
            }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        setCurrentcatin=1
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

