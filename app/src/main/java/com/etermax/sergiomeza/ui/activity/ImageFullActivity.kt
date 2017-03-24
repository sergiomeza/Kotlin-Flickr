package com.etermax.sergiomeza.ui.activity

import android.annotation.SuppressLint
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View

import com.etermax.sergiomeza.R
import com.etermax.sergiomeza.model.FlickParams
import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.util.Consts
import com.etermax.sergiomeza.util.loadFromFlickr
import kotlinx.android.synthetic.main.activity_image_full.*

class ImageFullActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = object : Runnable {
        @SuppressLint("InlinedApi")
        override fun run() {
            mImgFullScreen!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private var mControlsView: View? = null
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        val actionBar = supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_full)
        mVisible = true
        mControlsView = findViewById(R.id.fullscreen_content_controls)

        //ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled (true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        supportActionBar?.elevation = 0f;

        //INICIALIZAR DATOS
        mImgFullScreen.setOnClickListener { toggle() }
        if(intent.extras.containsKey(Consts.DETAIL_DATA)) {
            val mPhoto = intent.getSerializableExtra(Consts.DETAIL_DATA) as Photo
            if(mPhoto.url_s != null && mPhoto.url_s.isNotEmpty()) {//COMPROBAR SI LA URL DESDE EL API NO ESTE VACIO
                mImgFullScreen.loadFromFlickr(mPhoto.url_s)
            }
            else {
                //UTILIZAR EL TOSTRING DEL MODELO FLICK PARAMS PARA ARMAR LA URL ESTATICA ALTERNATIVA
                mImgFullScreen.loadFromFlickr(FlickParams(mPhoto.farm, mPhoto.server, mPhoto.id,
                        mPhoto.secret).toString())
            }

            mTxtFlickNameUser.text = mPhoto.ownername
            mTxtFlickTitle.text = mPhoto.title
            mTxtFlickViews.text = getString(R.string.views, mPhoto.views.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId) {
        android.R.id.home -> {
            supportFinishAfterTransition();
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        mImgFullScreen!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
