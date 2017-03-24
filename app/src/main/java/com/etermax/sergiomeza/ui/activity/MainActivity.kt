package com.etermax.sergiomeza.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.etermax.sergiomeza.R
import com.etermax.sergiomeza.model.Photo
import com.etermax.sergiomeza.model.Photos
import com.etermax.sergiomeza.util.EndlessRecyclerViewScrollListener
import com.etermax.sergiomeza.util.init
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.etermax.sergiomeza.util.ErrorView
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.etermax.sergiomeza.ui.adapter.MainAdapter
import com.etermax.sergiomeza.main.MainPresenter
import com.etermax.sergiomeza.main.MainView
import com.etermax.sergiomeza.util.Consts
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat



class MainActivity : AppCompatActivity(), MainView, (Photo) -> Unit {
    var mAdapter = MainAdapter(this)
    var mPresenter: MainPresenter? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var mPage = 1
    var mFlickPhotos: MutableList<Photo> = arrayListOf()
    var mCurrentLayout = 2;
    var mMenuItem: MenuItem? = null

    override fun invoke(photo: Photo) {

    }

    override fun showWait() {
        mProgressBar.visibility = View.VISIBLE
        mLinearError.visibility = View.GONE
        mRecycler.visibility = View.GONE
    }

    override fun onFilterFinish(mFlickrPhotos: MutableList<Photo>) {
        mAdapter.items = mFlickrPhotos
    }

    override fun onViewTypeChanged(mFlickrPhotos: MutableList<Photo>, mViewType: Int) {
        mAdapter.items = mFlickrPhotos
        mFlickPhotos = mAdapter.items //PARA MANEJO LOCAL
    }

    override fun removeWait(mError: Boolean) {
        mProgressBar.visibility = View.GONE
        mSwipeFrag.isRefreshing = false
        if(mError) {
            mLinearError.visibility = View.VISIBLE
            mRecycler.visibility = View.GONE
        }
        else {
            mLinearError.visibility = View.GONE
            mRecycler.visibility = View.VISIBLE
        }
    }

    override fun onFailure(mErrorMessage: String) {
        //MOSTRAR ERROR
        mLinearError.removeAllViews()
        mLinearError.addView(ErrorView(this).init(mErrorMessage))
    }

    override fun onFlickListSuccess(mFlickrPhotos: Photos, mFromPagination: Boolean) {
        if(mFlickrPhotos.total > 0){
            if(mFromPagination){
                val mSize = mAdapter.items.size
                mAdapter.items.removeAt(mSize - 1)
                mAdapter.items.addAll(mFlickrPhotos.photo as MutableList<Photo>)
                mFlickPhotos = mAdapter.items //PARA MANEJO LOCAL
                mRecycler.adapter.notifyItemRangeChanged(mSize - 1, mAdapter.items.size - mSize);
            }
            else {
                scrollListener?.resetState();
                mAdapter.items = mFlickrPhotos.photo as MutableList<Photo>
                mFlickPhotos = mAdapter.items // PARA MANEJO LOCAL
            }
        }
    }

    override fun onRefreshFinish() {
        mSwipeFrag.isRefreshing = false
        mAdapter.items.forEach { it.mViewType = mCurrentLayout }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //SE REFERENCIA PARA PODER CAMBIAR EN RUNTIME LOS ICONOS DEL ACTIONBAR
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if(savedInstanceState == null){
            mRecycler.init(LinearLayoutManager.VERTICAL)
            mRecycler.adapter = mAdapter
            addRecyclerListener()
        }

        mPresenter = MainPresenter(this, this)
        mPresenter?.getFlickPhotos()
        mSwipeFrag.setOnRefreshListener {
            mPresenter?.getFlickPhotos(mCurrentLayout)
        }
    }

    //AGREGAR EL SCROLL LISTEN A EL RECYCLER
    fun addRecyclerListener(){
        var mLayoutMan: RecyclerView.LayoutManager? = null
        if(mCurrentLayout == Consts.TYPE_CARD){
            mLayoutMan = mRecycler.layoutManager as LinearLayoutManager
        }
        else {
            mLayoutMan = mRecycler.layoutManager as GridLayoutManager
        }
        scrollListener = object : EndlessRecyclerViewScrollListener(
                if(mLayoutMan is LinearLayoutManager) mLayoutMan as LinearLayoutManager
                else mLayoutMan as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if(!hasFooter()) {
                    val mHandler = Handler()
                    mHandler.post {
                        mAdapter.items.add(Photo(mViewType = 3))
                        mRecycler.adapter.notifyItemInserted(mAdapter.items.size - 1)
                    }

                    mPage++
                    mPresenter?.getFlickPhotos(mPage = mPage, mFromPagination = true,
                            mViewType = mCurrentLayout)
                }
            }
        }
        mRecycler.addOnScrollListener(scrollListener)
    }

    private fun hasFooter(): Boolean {
        return mAdapter.items.get(mAdapter.items.size - 1).mViewType == 3;
    }

    //AL CREAR LAS OPCIONES DEL MENU

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //INFLAR EL MENU EN LA ACTIVIDAD
        menuInflater.inflate(R.menu.menu_main, menu);
        mMenuItem = menu?.findItem(R.id.action_list)

        //TRAER EL SEARCHVIEW
        val myActionMenuItem = menu?.findItem(R.id.action_search)
        val mSearchMenu = myActionMenuItem?.actionView as SearchView
        mSearchMenu.queryHint = getString(R.string.search_flickr)
        mSearchMenu.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!mSearchMenu.isIconified) {
                    mSearchMenu.isIconified = true
                }
                myActionMenuItem.collapseActionView()
                return true
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if(s?.isNotEmpty()!!){
                    val mSearchParams = s.toString().trim().toLowerCase()
                    //DECIRLE AL PRESENTER QUE ME FILTER LA LISTA CON LOS PARAMETROS DE BUSQUEDA
                    //ENVIADOS
                    mPresenter?.filterList(mAdapter.items, mSearchParams)
                }
                else {
                    mAdapter.items = mFlickPhotos
                }

                mAdapter.notifyDataSetChanged()
                mRecycler.scrollToPosition(0)

                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId) {
        R.id.action_list -> {
            changeRecyclerLayout(true)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //CAMBIAR EL LAYOUT DEL RECYCLERVIEW
    fun changeRecyclerLayout(mChangeViewType: Boolean = false){
        if(mCurrentLayout == Consts.TYPE_CARD) {
            val mGridLayoutManager = GridLayoutManager(this, 3)
            mRecycler.layoutManager = mGridLayoutManager
            mCurrentLayout = Consts.TYPE_GRID
            mMenuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_view_list_white_24dp);
        }
        else {
            val mLinearLayoutManager = LinearLayoutManager(this)
            mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            mRecycler.layoutManager = mLinearLayoutManager
            mCurrentLayout = Consts.TYPE_CARD
            mMenuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_grid_on_white_24dp);
        }

        addRecyclerListener()
        if(mChangeViewType)
            mPresenter?.changeViewType(mCurrentLayout, mAdapter.items)
    }
}
