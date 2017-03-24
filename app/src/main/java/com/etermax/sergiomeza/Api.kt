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
//    https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&per_page=50&api_key=65b3d15606c3df9a78c9cef5967b5916&format=json&nojsoncallback=1&extras=date_upload,url_o,owner_name,description
//    ?api_key=436f1bbafb897b0ae3a6f38a65b9a9f8&format=json&nojsoncallback=1&method=flickr.photos.getRecent&extras=date_upload,url_o,owner_name,description
    @GET("?api_key=436f1bbafb897b0ae3a6f38a65b9a9f8&format=json&nojsoncallback=1&method=flickr.photos.getRecent&extras=date_upload,url_o,owner_name,description,url_m,url_s,icon_server,views")
    fun getFlickFeed(@Query("page")page: Int = 1, @Query("per_page")per_page:Int = 50): Call<ResponseApi>
}