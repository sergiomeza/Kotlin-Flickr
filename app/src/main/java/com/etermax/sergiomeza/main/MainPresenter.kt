package com.etermax.sergiomeza.main

import android.content.Context
import com.etermax.sergiomeza.Api
import com.etermax.sergiomeza.R
import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.model.ResponseApi
import com.etermax.sergiomeza.util.isConnectingToInternet
import com.etermax.sergiomeza.util.retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by sergiomeza on 3/23/17.
 */
class MainPresenter(val mMainView: MainView, val mContext: Context) {
    //INICIALIZA EL SINGLETON DE RETROFIT DESDE EXTENSION METHOD
    val api = retrofit().create(Api::class.java)

    //DEVUELVE FOTOS DE FLICKKKR
    fun getFlickPhotos(mPage: Int = 1, mPerpage: Int = 30,
                       mFromPagination: Boolean = false){
        if(!mFromPagination){
            mMainView.showWait()
        }
        
        if(mContext.isConnectingToInternet()) {
            val mCall = api.getFlickFeed(mPage, mPerpage)
            mCall.enqueue(object : Callback<ResponseApi> {
                override fun onResponse(call: Call<ResponseApi>?, response: Response<ResponseApi>?) {
                    var mError = false
                    try {
                        if (response?.body() != null) {
                            mMainView.onFlickListSuccess(response.body().photos, mFromPagination)
                        }
                    } catch (e: Exception) {
                        mMainView.onFailure("${e.message} .. ${e.cause}")
                        call?.cancel()
                        mError = true
                    }

                    mMainView.removeWait(mError)
                    mMainView.onRefreshFinish()
                }

                override fun onFailure(call: Call<ResponseApi>?, t: Throwable?) {
                    mMainView.removeWait(true)
                    mMainView.onFailure("${t?.message} .. ${t?.cause}")
                }
            })
        }
        else {
            mMainView.removeWait(true)
            mMainView.onFailure(mContext.getString(R.string.no_internet_connection))    
        }
    }

    //FILTRAR LISTA DE FOTOS POR OWNERNAME O TITULO
    fun filterList(mListPhotos: MutableList<Photo>, mSearchParams: String){
        val mNewFlickrPics = mListPhotos.filter {
            it.title.toLowerCase().contains(mSearchParams)
            || it.ownername.toLowerCase().contains(mSearchParams)
        }

        mMainView.onFilterFinish(mNewFlickrPics as MutableList<Photo>)
    }
}