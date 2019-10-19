package com.NasApp

import android.animation.ObjectAnimator
import android.content.Context
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.messaging.FirebaseMessaging
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.instrument_cell.view.*
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init mapbox token
        setupMapbox(savedInstanceState)

        //Init a Firebase subscription
        setupFirebase()

        //UI listnener config
        sat_name.text = sat.name
        lap_time.text = sat.formattedLapTime()

        instrumentsList.layoutManager = LinearLayoutManager(this)
        instrumentsList.adapter = InstrumentsAdapter(sat.instruments, this)

        val sheetBehavior = BottomSheetBehavior.from(bottom_sheet)// click event for show-dismiss bottom sheet
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d(TAG, "Hiden")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d(TAG, "Expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d(TAG, "Expanded")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                }
            }

            override fun onSlide(view: View, v: Float) {
            }
        })

        startAnimationJob()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun setupMapbox(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.mapkey))
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                mapboxMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(sat.location.latitude, sat.location.longitude))
                        .title(sat.name)
                )

                val position = CameraPosition.Builder()
                    .target(LatLng(sat.location.latitude, sat.location.longitude))
                    .zoom(10.0)
                    .tilt(20.0)
                    .build()

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 200)
            }
        }

    }

    private fun startAnimationJob() {
        val durationMs = 2000L
        val displacement = 50f
        Thread {
            while(true){
                runOnUiThread {
                    ObjectAnimator.ofFloat(nasAppImg, "translationY", displacement).apply {
                        duration = durationMs
                        start()
                    }
                }
                Thread.sleep(durationMs)
                runOnUiThread {
                    ObjectAnimator.ofFloat(nasAppImg, "translationY", -displacement).apply {
                        duration = durationMs
                        start()
                    }
                }
                Thread.sleep(durationMs)
            }
        }.start()
    }

    fun setupFirebase(){
        FirebaseMessaging.getInstance().subscribeToTopic("nasagral")
            .addOnCompleteListener { task ->
                Log.d(TAG, "Already subscribed to topic nasagral")
        }
    }

    companion object {
        val TAG:String = "MainActivity"
        val sat = createMockSat()

        fun createMockSat(): Satellite {
            return Satellite(
                "Landsat 8",
                listOf(
                    Instrument("Operational Land Imager USGS (NASA)",
                        listOf("Surface radiance", "Land cover state and change",
                            "Multi-purpose imagery for land applications")),
                    Instrument("Thermal Infrared Sensor",
                        listOf("Surface emittance", "Land cover state and change",
                            "Multi-purpose imagery for land applications"))
                ),
                (16 * 24 * 3600) - 453, //~16 dias
                Location("").apply {
                    latitude = -31.4254801
                    longitude = -64.1920586
                }
            )
        }

    }

}

class InstrumentsAdapter(val dataSet: List<Instrument>, val context:Context) : RecyclerView.Adapter<InstrumentsAdapter.InstrumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrumentViewHolder {
        return InstrumentViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.instrument_cell,parent,false))
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        val data = dataSet[position]
        var lines = ""
        holder.nameView.text = data.name
        for (desc in data.measuresDescription){
            lines += "\uD83D\uDE80 $desc\n"
        }
        holder.descView.text = lines
    }

    class InstrumentViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val nameView = view.card_instrument_title
        val descView = view.card_instrument_description
    }

}


