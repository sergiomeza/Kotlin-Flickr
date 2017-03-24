package com.etermax.sergiomeza.main

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.etermax.sergiomeza.R
import com.etermax.sergiomeza.model.FlickParams
import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_CARD
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_FOOTER
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_GRID
import com.etermax.sergiomeza.util.loadFromFlickr
import kotlin.properties.Delegates

/**
 * Created by sergiomeza on 3/23/17.
 */
class MainAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /*
        BY DELEGATES ES UN METODO NATIVO DE KOTLIN PARA DELEGAR EVENTOS QUE OCURRAN DENTRO DE LA LISTA
        EN ESTE CASO SE DELEGA QUE CUANDO SE AGREGUE O SE ELIMINE ALGUN ITEM DE LA LISTA SE DISPARE
        EL METODO DEL RECYCLERVIEW NOTIFYDATA...
     */
    var items: MutableList<Photo> by Delegates.observable(mutableListOf(),
            { _, _, _ -> notifyDataSetChanged() })

    override fun getItemCount() =  items.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        //SE COMPARA PARA SABER QUE HOLDER UTILIZAR, SI EL GRID O EL DEL CARDVIEW
        when(holder){
            is ViewHolderCard -> {
                //SMART CAST POR KOTLIN
                holder.bindView(items[position])
            }
            is ViewHolderGrid -> {
                //SMART CAST POR KOTLIN
                holder.bindView(items[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].mViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val mInflater = LayoutInflater.from(parent?.context)
        val mViewHolder : RecyclerView.ViewHolder
        when(viewType){
            TYPE_CARD -> {
                val v = mInflater.inflate(R.layout.flickr_item_card, parent, false)
                mViewHolder = MainAdapter.ViewHolderCard(v)
            }
            TYPE_FOOTER -> {
                val v = mInflater.inflate(R.layout.flickr_item_progress, parent, false)
                mViewHolder = MainAdapter.ViewHolderFooter(v)
            }
            TYPE_GRID -> {
                val v = mInflater.inflate(R.layout.flickr_item_grid, parent, false)
                mViewHolder = MainAdapter.ViewHolderGrid(v)
            }
            else -> {
                val v = mInflater.inflate(R.layout.flickr_item_card, parent, false)
                mViewHolder = MainAdapter.ViewHolderCard(v)
            }
        }

        return mViewHolder
    }

    //PROGRESSVIEWHOLDER
    class ViewHolderFooter(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var mProg: ProgressBar =  itemView.findViewById(R.id.mProgressRec) as ProgressBar
    }

    //GRID VIEWHOLDER
    class ViewHolderGrid(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var mImg: ImageView =  itemView.findViewById(R.id.mImgFlickGrid) as ImageView

        fun bindView(mPhoto: Photo){
            if(mPhoto.url_s != null && mPhoto.url_s.isNotEmpty()) {//COMPROBAR SI LA URL DESDE EL API NO ESTE VACIO
                this.mImg.loadFromFlickr(mPhoto.url_s)
            }
            else {
                //UTILIZAR EL TOSTRING DEL MODELO FLICK PARAMS PARA ARMAR LA URL ESTATICA ALTERNATIVA
                this.mImg.loadFromFlickr(FlickParams(mPhoto.farm, mPhoto.server, mPhoto.id,
                        mPhoto.secret).toString())
            }
        }
    }

    //CARD VIEWHOLDER
    class ViewHolderCard(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var mTxtName: TextView =  itemView.findViewById(R.id.mTxtFlickNameUser) as TextView
        var mTxtTitle: TextView =  itemView.findViewById(R.id.mTxtFlickTitle) as TextView
        var mTxtDate: TextView =  itemView.findViewById(R.id.mTxtFlickDate) as TextView
        var mImgUser: ImageView = itemView.findViewById(R.id.mImgFlickUser) as ImageView
        var mImgMain: ImageView = itemView.findViewById(R.id.mImgFlick) as ImageView

        //INICIALIZAR VISTA DEL HOLDER AL RECYCLERVIEW
        fun bindView(mPhoto: Photo){
            if(mPhoto.url_s != null && mPhoto.url_s.isNotEmpty()) {//COMPROBAR SI LA URL DESDE EL API NO ESTE VACIO
                this.mImgMain.loadFromFlickr(mPhoto.url_s)
            }
            else {
                //UTILIZAR EL TOSTRING DEL MODELO FLICK PARAMS PARA ARMAR LA URL ESTATICA ALTERNATIVA
                this.mImgMain.loadFromFlickr(FlickParams(mPhoto.farm, mPhoto.server, mPhoto.id,
                        mPhoto.secret).toString())
            }
            mTxtName.text = mPhoto.ownername
            mTxtTitle.text = if(mPhoto.title.length > 30) "${mPhoto.title.substring(0, 30)}..." else mPhoto.title

            //TODO: USERIMAGE
        }

    }
}