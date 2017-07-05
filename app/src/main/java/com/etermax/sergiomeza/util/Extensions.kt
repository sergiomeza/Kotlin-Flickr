package com.etermax.sergiomeza.util

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sergiomeza on 3/23/17.
 */

//EXTENSION PARA CARGAR IMAGEN DE FLICK CON PICASSO
fun ImageView.loadFromFlickr(mFlickUrl: String, mCircle: Boolean = false){
    val mPicasso = Picasso.with(this.context)
            .load(mFlickUrl)
    if(mCircle) {
        mPicasso.fit()
        mPicasso.transform(CircleTransform(this.context, false))
    }

    mPicasso.into(this)
}

//INICIALIZAR RECYCLERVIEW
fun RecyclerView.init(mOrientation: Int = LinearLayoutManager.VERTICAL){
    this.setHasFixedSize(true);
    val llm = LinearLayoutManager(this.context)
    llm.orientation = mOrientation
    this.layoutManager = llm
    this.itemAnimator = DefaultItemAnimator()
}

//SI ESTA CONECTADO O NO A INTERNET
fun Context.isConnectingToInternet(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as
            ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
}

//RETROFIT SINGLETON
fun retrofit(mUrl: String = Consts.URL_API) : Retrofit {
    val mRetrofit = Retrofit.Builder()
            .baseUrl(mUrl)
            .addConverterFactory(GsonConverterFactory.create())
    val httpClient = OkHttpClient.Builder()
    mRetrofit.client(httpClient.addInterceptor { chain ->
        val mOriginal = chain.request()
        val mUrl = mOriginal.url().newBuilder()
                .addQueryParameter("api_key", Consts.API_KEY)
                .build()
        val requestBuilder = mOriginal.newBuilder()
                .url(mUrl)
        chain.proceed(requestBuilder.build())}.build())

    return mRetrofit.build()
}

//PARSEAR FECHA AL FORMATO DESEADO
fun Date.parseToFormat(mFormat:String):String {
    val mOutputFormat = SimpleDateFormat(mFormat, Locale("es_CO"))
    return mOutputFormat.format(this)
}

//CONSTANTES
class Consts() {
    //COMPANION OBJECT ACTUA COMO STATIC EN JAVA
    companion object {
        val API_KEY ="<YOUR-API-KEY>"
        var URL_API = "https://api.flickr.com/services/rest/"

        //LAYOUTS TYPES
        val TYPE_GRID = 1
        val TYPE_CARD = 2
        val TYPE_FOOTER = 3

        val DETAIL_DATA = "DETAIL_DATA"
    }
}
