package com.NasApp

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*

data class Instrument(
    val name:String,
    val measuresDescription:List<String>
)

data class Satellite(
    val name:String,
    val instruments: List<Instrument>,
    val lapTime:Int,
    val location:Location
) {
    fun formattedLapTime(): String {
        return "" + lapTime / (60*60*24) + "d" +
            " " + (lapTime/(60*60)) % (24) + "h" +
            " " + (lapTime/60) % (60) + "m" +
            " " + lapTime % 60 + "s"
    }
}