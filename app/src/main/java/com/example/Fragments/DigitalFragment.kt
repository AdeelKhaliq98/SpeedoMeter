package com.example.Fragments

import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.Utils.GpsUtils
import com.example.ViewModels.ItemViewModel
import com.example.speedometer.R


class DigitalFragment : Fragment() {

    private val viewModel: ItemViewModel by activityViewModels()

    private lateinit var digitalspeedometerdigit: TextView
    private lateinit var digitalavgSpeed: TextView
    private lateinit var digitaldistance: TextView
    private lateinit var digitalmaxSpeed: TextView
    private lateinit var startPauseIcon: ImageView
    private lateinit var startButton: View
    private lateinit var timeTV: TextView
    private lateinit var startTrip: TextView
    private lateinit var accuracyDigit: TextView
    private var mLastClickTime: Long = 0



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
                onItemClicked(it)
            }
        })

        start()


        val view = inflater.inflate(R.layout.fragment_digital, container, false)
        timeTV = view.findViewById(R.id.digitalFragmentTimeTV)
        digitalspeedometerdigit = view.findViewById(R.id.digital_digit_speedview)
        digitalavgSpeed = view.findViewById(R.id.digitalAvgSpeedDigit)
        digitaldistance = view.findViewById(R.id.digitalDistanceDigit)
        digitalmaxSpeed = view.findViewById(R.id.digitalMaxSpeedDigit)
        accuracyDigit = view.findViewById(R.id.digitalAccuracyDigit)

        startTrip = view.findViewById(R.id.digitalStartTrip)
        startPauseIcon = view.findViewById(R.id.digitalStartIcon)

        startButton = view.findViewById(R.id.digitalStartImageBtn)

        startButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return@setOnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isClicked) {
                startPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                startButton.setBackgroundResource(R.drawable.button_background)
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
                startPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24)
                startButton.setBackgroundResource(R.drawable.stopbutton_background)
                onItemClicked(isClicked)
            }
        }

        return view
    }

    fun onItemClicked(item: Boolean) {

        viewModel.fragmentToActivityButtonState(item)
    }

    fun onButtonStateChange(item: Boolean) {
        viewModel.FtoFButtonState(item)
    }

    private fun start() {
        viewModel.timeObserver.observe(viewLifecycleOwner, Observer {
            timeTV.text = it
        })
        viewModel.comingItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (isClicked == true) {
                if (it != null) {
                    digitalspeedometerdigit.text = it.accurateSpeed.toString()
                    digitaldistance.text = it.travelDistance.toString()
                    digitalmaxSpeed.text = it.MaxSpeed.toString()
                    digitalavgSpeed.text = it.averageSpeed.toString()
                    accuracyDigit.text = it.accuracy.toString()
                }
            } else {
                digitalspeedometerdigit.text = "0"
                digitaldistance.text = "0.00"
                digitalmaxSpeed.text = "0"
                digitalavgSpeed.text = "0"
                accuracyDigit.text = "0"
                timeTV.text = "00:00:00"
            }
        })
    }
}