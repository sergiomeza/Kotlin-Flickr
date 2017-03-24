package com.etermax.sergiomeza.model

/**
 * Created by sergiomeza on 3/24/17.
 */
data class Photos(val page: Int, val pages: Int, val perpage: Int, val total: Int,
                  val photo: List<Photo>) {
}