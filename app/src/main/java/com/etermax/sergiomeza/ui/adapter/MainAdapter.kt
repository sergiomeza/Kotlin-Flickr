package com.etermax.sergiomeza.ui.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.BundleCompat
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
import com.etermax.sergiomeza.ui.activity.DetailActivity
import com.etermax.sergiomeza.util.Consts
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_CARD
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_FOOTER
import com.etermax.sergiomeza.util.Consts.Companion.TYPE_GRID
import com.etermax.sergiomeza.util.loadFromFlickr
import kotlin.properties.Delegates
import android.support.v4.view.ViewCompat.getTransitionName



/**
 * Created by sergiomeza on 3/23/17.
 */
class MainAdapter(val listener: (Photo) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                holder.bindView(items[position], listener)
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
                mViewHolder = ViewHolderCard(v)
            }
            TYPE_FOOTER -> {
                val v = mInflater.inflate(R.layout.flickr_item_progress, parent, false)
                mViewHolder = ViewHolderFooter(v)
            }
            TYPE_GRID -> {
                val v = mInflater.inflate(R.layout.flickr_item_grid, parent, false)
                mViewHolder = ViewHolderGrid(v)
            }
            else -> {
                val v = mInflater.inflate(R.layout.flickr_item_card, parent, false)
                mViewHolder = ViewHolderCard(v)
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

        fun bindView(mPhoto: Photo,  listener: (Photo) -> Unit){
            if(mPhoto.url_s != null && mPhoto.url_s.isNotEmpty()) {//COMPROBAR SI LA URL DESDE EL API NO ESTE VACIO
                this.mImg.loadFromFlickr(mPhoto.url_s)
            }
            else {
                //UTILIZAR EL TOSTRING DEL MODELO FLICK PARAMS PARA ARMAR LA URL ESTATICA ALTERNATIVA
                this.mImg.loadFromFlickr(FlickParams(mPhoto.farm, mPhoto.server, mPhoto.id,
                        mPhoto.secret).toString())
            }

            itemView.setOnClickListener {
                val mIntentDetail = Intent(itemView.context, DetailActivity::class.java)
                mIntentDetail.putExtra(Consts.DETAIL_DATA, mPhoto)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,
                        this.mImg, itemView.context.getString(R.string.transition_image))
                ActivityCompat.startActivity(itemView.context as Activity,
                        mIntentDetail, options.toBundle())
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
            mTxtDate.text = mPhoto.getDateFormated()
            mImgUser.loadFromFlickr(mPhoto.getUserPhotoUrl(), true)

            itemView.setOnClickListener {
                val mIntentDetail = Intent(itemView.context, DetailActivity::class.java)
                mIntentDetail.putExtra(Consts.DETAIL_DATA, mPhoto)
                val mPairImg: android.support.v4.util.Pair<View, String> =
                        android.support.v4.util.Pair.create(this.mImgMain,
                                itemView.context.getString(R.string.transition_image))
                val mPairTitle: android.support.v4.util.Pair<View, String> =
                        android.support.v4.util.Pair.create(this.mTxtTitle,
                                itemView.context.getString(R.string.transition_title))
                val mPairUserPic: android.support.v4.util.Pair<View, String> =
                        android.support.v4.util.Pair.create(this.mImgUser,
                                itemView.context.getString(R.string.transition_image_user))
                val mOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,
                        mPairImg, mPairTitle, mPairUserPic)
                ActivityCompat.startActivity(itemView.context as Activity,
                        mIntentDetail, mOptions.toBundle())
            }
        }

    }
}