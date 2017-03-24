package com.etermax.sergiomeza.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.etermax.sergiomeza.R

/**
 * Created by sergiomeza on 3/24/17.
 */
class ErrorView(val mContext : Context) {
    fun init(mMessage: Int = R.string.error_empty_data): View {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.app_status_view, null)
        val mText = mView.findViewById(R.id.mTxtMessageStatus) as TextView
        mText.text = mContext.getText(mMessage)
        return mView
    }
}