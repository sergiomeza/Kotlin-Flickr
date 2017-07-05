package com.etermax.sergiomeza

import com.etermax.sergiomeza.model.Photos
import com.etermax.sergiomeza.model.ResponseApi
import com.etermax.sergiomeza.util.Consts
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sergiomeza on 3/23/17.
 */

interface Api {
    @GET("?format=json&nojsoncallback=1&method=flickr.photos.getRecent&extras=date_upload,url_o,owner_name,description,url_m,url_s,icon_server,views")
    fun getFlickFeed(@Query("page")page: Int = 1, @Query("per_page")per_page:Int = 50): Call<ResponseApi>
}