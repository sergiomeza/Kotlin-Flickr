package com.etermax.sergiomeza.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
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
import android.view.MenuInflater
import com.etermax.sergiomeza.util.ErrorView


class MainActivity : AppCompatActivity(), MainView {
    var mAdapter = MainAdapter()
    var mPresenter: MainPresenter? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var mPage = 1
    var mFlickPhotos: MutableList<Photo> = arrayListOf()

    override fun showWait() {
        mProgressBar.visibility = View.VISIBLE
        mLinearError.visibility = View.GONE
        mRecycler.visibility = View.GONE
    }

    override fun onFilterFinish(mFlickrPhotos: MutableList<Photo>) {
        mAdapter.items = mFlickrPhotos
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            mRecycler.init(LinearLayoutManager.VERTICAL)
            mRecycler.adapter = mAdapter
            scrollListener = object : EndlessRecyclerViewScrollListener(mRecycler.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    if(!hasFooter()) {
                        val mHandler = Handler()
                        mHandler.post {
                            mAdapter.items.add(Photo(mViewType = 3))
                            mRecycler.adapter.notifyItemInserted(mAdapter.items.size - 1)
                        }

                        mPage++
                        mPresenter?.getFlickPhotos(mPage = mPage, mFromPagination = true)
                    }
                }
            }
            mRecycler.addOnScrollListener(scrollListener)
        }

        mPresenter = MainPresenter(this, this)
        mPresenter?.getFlickPhotos()
        mSwipeFrag.setOnRefreshListener {
            mPresenter?.getFlickPhotos()
        }
    }

    private fun hasFooter(): Boolean {
        return mAdapter.items.get(mAdapter.items.size - 1).mViewType == 3;
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //INFLAR EL MENU EN LA ACTIVIDAD
        menuInflater.inflate(R.menu.menu_main, menu);
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
}
