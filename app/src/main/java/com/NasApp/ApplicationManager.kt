package com.NasApp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.mapbox.mapboxsdk.Mapbox

class ApplicationManager:Application(){
    override fun onCreate() {
        super.onCreate()
        //Night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        Mapbox.getInstance(applicationContext, getString(R.string.mapkey))
    }
}