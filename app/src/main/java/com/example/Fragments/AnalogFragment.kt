package com.example.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.Utils.GpsUtils.locationCheck
import com.example.ViewModels.ItemViewModel
import com.example.speedometer.R
import com.github.anastr.speedviewlib.PointerSpeedometer
import pub.devrel.easypermissions.AppSettingsDialog


class AnalogFragment : Fragment() {


    private var isClicked = false

    private lateinit var startButton: View
    private lateinit var avgSpeed: TextView
    private lateinit var timeTV: TextView
    private lateinit var distance: TextView
    private lateinit var maxSpeed: TextView
    private lateinit var awesomeSpeedometer: PointerSpeedometer
    private lateinit var accuracyDigit: TextView
    private var mLastClickTime: Long = 0

    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_analog, container, false)
        avgSpeed = view.findViewById(R.id.avgspeeddigit)
        timeTV = view.findViewById(R.id.time)
        distance = view.findViewById(R.id.distancedigit)
        maxSpeed = view.findViewById(R.id.maxspeeddigit)
        accuracyDigit = view.findViewById(R.id.analogAccuracyDigit)
        var startTrip = view.findViewById<TextView>(R.id.startTrip)
        var startPauseIcon = view.findViewById<ImageView>(R.id.icon)

        awesomeSpeedometer = view.findViewById(R.id.pointerSpeedometer)
        awesomeSpeedometer.withTremble = false

        viewModel.selectedState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it

            if (it == false) {
                isClicked = false
                start()
                startPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                startButton.setBackgroundResource(R.drawable.button_background)
                startTrip.setText("Start Trip")
            } else {
                isClicked = true
                startTrip.setText("Stop Trip")
                startPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24)
                startButton.setBackgroundResource(R.drawable.stopbutton_background)
                onButtonClicked(it)
            }
        })
        start()

        startButton = view.findViewById<View>(R.id.image_btn)
            startButton.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return@setOnClickListener;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isClicked) {
                    isClicked = false
                    startPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    startButton.setBackgroundResource(R.drawable.button_background)
                    startTrip.setText("Start Trip")
                    locationCheck(null)
                    onButtonClicked(isClicked)
                    onButtonStateChange(isClicked)

                }

                else {
                    isClicked = true
                    onButtonClicked(isClicked)
                    onButtonStateChange(isClicked)
                    start()
                    startTrip.setText("Stop Trip")
                    startPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24)
                    startButton.setBackgroundResource(R.drawable.stopbutton_background)
                }
            }

        return view
    }

    fun onButtonClicked(item: Boolean) {
        viewModel.fragmentToActivityButtonState(item)
    }

    fun onButtonStateChange(item: Boolean) {
        viewModel.FtoFButtonState(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(requireContext(), "onActivtyResult", Toast.LENGTH_SHORT).show()
        }
    }

    private fun start() {
        viewModel.timeObserver.observe(viewLifecycleOwner, Observer {
            timeTV.text = it
        })
        viewModel.comingItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (isClicked == true) {
                if (it != null) {
                    it.accurateSpeed?.let { it1 -> awesomeSpeedometer.speedTo(it1.toFloat()) }
                    distance.text = it.travelDistance.toString()
                    maxSpeed.text = it.MaxSpeed.toString()
                    avgSpeed.text = it.averageSpeed.toString()
                    accuracyDigit.text = it.accuracy.toString()
                }
            } else {
                awesomeSpeedometer.speedTo(0f)
                distance.text = "0.00"
                maxSpeed.text = "0"
                avgSpeed.text = "0"
                accuracyDigit.text = "0"
                timeTV.text = "00:00:00"
            }
        })
    }
}