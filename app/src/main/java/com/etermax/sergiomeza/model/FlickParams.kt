package com.etermax.sergiomeza.model

import retrofit2.http.Path

/**
 * Created by sergiomeza on 3/23/17.
 */
class FlickParams(val farm_id: Int, val server_id: String,
                  val id: String, val secret: String) {

    //DEVUELVE URL STATIC DE LA FOTO DE FLICKR
    override fun toString(): String {
        return "http://farm${farm_id}.static.flickr.com/${server_id}/${id}_${secret}.jpg"
    }
}