package com.etermax.sergiomeza.model

import java.io.Serializable

/**
 * Created by sergiomeza on 3/24/17.
 */
data class Photo(val id: String = "", val owner: String= "", val secret: String= "", val title: String= "",
                 val farm: Int= 0, val dateupload: String= "", val ownername: String= "", val url_o: String= "",
                 val description: Description? = null, val server: String= "", val url_m: String= "",
                 val url_s: String= "", var mViewType: Int = 2): Serializable {

    fun getDateFormated(): String {
        return this.dateupload
    }

    data class Description(val _content: String): Serializable {}
}