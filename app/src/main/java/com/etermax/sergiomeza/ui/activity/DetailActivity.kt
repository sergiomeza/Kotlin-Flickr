package com.etermax.sergiomeza.ui.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.View

import com.etermax.sergiomeza.R
import com.etermax.sergiomeza.model.FlickParams
import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.util.Consts
import com.etermax.sergiomeza.util.loadFromFlickr
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var mPhoto: Photo? = null
    val mFlickUserUri = "https://www.flickr.com/images/buddyicon.gif"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //TOOLBAR INIT
        setSupportActionBar(mToolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        mToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener {
            supportFinishAfterTransition();
        }

        //INICIALIZAR
        if(intent.extras.containsKey(Consts.DETAIL_DATA)){
            mPhoto = intent.getSerializableExtra(Consts.DETAIL_DATA) as Photo
            mCollapsingDetail.setTitle(mPhoto?.title);
            if(mPhoto?.url_s != null && mPhoto?.url_s?.isNotEmpty()!!) {//COMPROBAR SI LA URL DESDE EL API NO ESTE VACIO
                mImgDetail.loadFromFlickr(mPhoto?.url_s!!)
            }
            else {
                //UTILIZAR EL TOSTRING DEL MODELO FLICK PARAMS PARA ARMAR LA URL ESTATICA ALTERNATIVA
                mImgDetail.loadFromFlickr(FlickParams(mPhoto?.farm!!, mPhoto?.server!!, mPhoto?.id!!,
                        mPhoto?.secret!!).toString())
            }
            mTxtFlickNameUser.text = mPhoto?.ownername
            mTxtFlickTitle.text = mPhoto?.title
            mTxtFlickDescription.text = Html.fromHtml(mPhoto?.description?._content)
            mImgFlickUser.loadFromFlickr(mPhoto?.getUserPhotoUrl()!!, true)
            mTxtFlickDate.text = mPhoto?.getDateFormated()

            mImgDetail.setOnClickListener {
                val mIntentDetail = Intent(this, ImageFullActivity::class.java)
                mIntentDetail.putExtra(Consts.DETAIL_DATA, mPhoto)
                val mPairImg: android.support.v4.util.Pair<View, String> =
                        android.support.v4.util.Pair.create(this.mImgDetail,
                                getString(R.string.transition_image))
                val mOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        mPairImg)
                ActivityCompat.startActivity(this,
                        mIntentDetail, mOptions.toBundle())
            }
        }
    }
}
