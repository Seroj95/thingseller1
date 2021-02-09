package com.sunnyschool.thingseller.`interface`

import android.view.View

interface ItemClickListner {
    fun onClick(view: View?, position: Int, isLongClick: Boolean)
}