package com.etermax.sergiomeza.model

import com.etermax.sergiomeza.util.parseToFormat
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sergiomeza on 3/24/17.
 */
data class Photo(val id: String = "", val owner: String= "", val secret: String= "", val title: String= "",
                 val farm: Int= 0, val dateupload: String= "", val ownername: String= "", val url_o: String= "",
                 val description: Description? = null, val server: String= "", val url_m: String= "",
                 val url_s: String= "", var mViewType: Int = 2, val iconserver: String ="",
                 val iconfarm: Int = 0, val views: Int = 0): Serializable {

    fun getDateFormated(): String {
        val mDate = Date(this.dateupload.toInt() * 1000L)
        val mInputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
        mInputFormat.timeZone = TimeZone.getTimeZone("UTC-8")
        try {
            val mDateOut = mInputFormat.parse(mInputFormat.format(mDate))
            return "${mDateOut.parseToFormat("d MMM")}, ${mDateOut.parseToFormat("yyyy")}"
        } catch (e: ParseException) {
            e.printStackTrace()
            return "NaN"
        }
    }

    fun getUserPhotoUrl(): String{
        return "http://farm${iconfarm}.staticflickr.com/${iconserver}/buddyicons/${owner}.jpg"
    }

    data class Description(val _content: String): Serializable {}
}