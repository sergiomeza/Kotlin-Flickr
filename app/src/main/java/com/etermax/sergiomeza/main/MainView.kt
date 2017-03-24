package com.etermax.sergiomeza.main

import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.model.Photos

/**
 * Created by sergiomeza on 3/23/17.
 */
interface MainView {
    fun showWait()

    fun removeWait(mError: Boolean)

    fun onFailure(mErrorMessage: String)

    fun onFlickListSuccess(mFlickrPhotos: Photos, mFromPagination: Boolean = false)

    fun onRefreshFinish()

    fun onFilterFinish(mFlickrPhotos: MutableList<Photo>)

    fun onViewTypeChanged(mFlickrPhotos: MutableList<Photo>, mViewType: Int)
}